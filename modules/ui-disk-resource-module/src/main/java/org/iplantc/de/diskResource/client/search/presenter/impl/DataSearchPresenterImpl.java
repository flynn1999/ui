package org.iplantc.de.diskResource.client.search.presenter.impl;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent.HasDeleteSavedSearchEventHandlers;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasName;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSearchPresenterImpl implements DataSearchPresenter {

    List<DiskResourceQueryTemplate> cleanCopyQueryTemplates = Lists.newArrayList();
    final List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList();
    DiskResourceSearchField searchField;
    private DiskResourceQueryTemplate activeQuery = null;
    private final IplantAnnouncer announcer;
    private HandlerManager handlerManager;
    private final SearchServiceFacade searchService;
    private final EventBus eventBus;

    @Inject
    public DataSearchPresenterImpl(final SearchServiceFacade searchService,
            final IplantAnnouncer announcer, final EventBus eventBus) {
        this.searchService = searchService;
        this.announcer = announcer;
        this.eventBus = eventBus;
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectedEvent.FolderSelectedEventHandler handler) {
        return ensureHandlers().addHandler(FolderSelectedEvent.TYPE, handler);
    }

    /**
     * This handler is responsible for saving or updating the {@link DiskResourceQueryTemplate} contained
     * in the given {@link org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent}.
     * <p/>
     * After the query has been successfully saved, a search with the given querytemplate will be
     * performed.
     */
    @Override
    public void doSaveDiskResourceQueryTemplate(final SaveDiskResourceQueryEvent event) {
        // Assume that once the filter is saved, a search should be performed.
        final DiskResourceQueryTemplate queryTemplate = event.getQueryTemplate();

        if (Strings.isNullOrEmpty(queryTemplate.getName())) {
            // Given query template has no name, ripple error back to view
            GWT.log("TODO: User tried to save query with no name, cannot save. Ripple error back to view");
            return;
        } else {
            // Check for name uniqueness
            final Set<String> uniqueNames = getUniqueNames(getQueryTemplates());
            if (uniqueNames.size() == getQueryTemplates().size()) {
                // Sanity check: There were no dupes in the current list
                if (uniqueNames.contains(queryTemplate.getName())) {
                    /*
                     * The given query template is already in the list, remove it. The new one will be
                     * added to the list submitted to the service.
                     */
                    for (DiskResourceQueryTemplate hasId : ImmutableList.copyOf(getQueryTemplates())) {
                        String inListName = hasId.getName();
                        if (queryTemplate.getName().equalsIgnoreCase(inListName)) {
                            getQueryTemplates().remove(hasId);

                            break;
                        }
                    }
                }
            }
        }

        final ImmutableList<DiskResourceQueryTemplate> toBeSaved = ImmutableList.copyOf(Iterables.concat(queryTemplates, Collections.singletonList(queryTemplate)));
        // Call service to save template
        searchService.saveQueryTemplates(toBeSaved, new AsyncCallback<List<DiskResourceQueryTemplate>>() {

            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig("Unable to save filter."));
            }

            @Override
            public void onSuccess(List<DiskResourceQueryTemplate> savedTemplates) {
                // Clear list of saved query templates and re-add result.
                queryTemplates.clear();
                if (toBeSaved.size() != savedTemplates.size()) {
                    GWT.log("Saved templates returned from search service facade is a different size than what we submitted.");
                }
                queryTemplates.addAll(savedTemplates);

                /*
                 * Determine if there has been a name change, if so, remove the original from the
                 * treestore.
                 */
                List<DiskResourceQueryTemplate> queriesToRemove = Lists.newArrayList();
                for (DiskResourceQueryTemplate qt : cleanCopyQueryTemplates) {
                    if (qt.getName().equals(event.getOriginalName())) {
                        queriesToRemove.add(qt);
                    }
                }
                // Create immutable copy of saved templates
                setCleanCopyQueryTemplates(searchService.createFrozenList(toBeSaved));

                List<DiskResourceQueryTemplate> toUpdate = Lists.newArrayList();
                // If it is an existing query, determine if it is dirty. If so, set dirty flag
                if (templateHasChanged(queryTemplate, cleanCopyQueryTemplates)) {
                    queryTemplate.setDirty(true);
                    // Replace existing object in current template list
                    for (DiskResourceQueryTemplate qt : getQueryTemplates()) {
                        if (qt.getName().equalsIgnoreCase(queryTemplate.getName())) {
                            toUpdate.add(queryTemplate);
                        } else {
                            toUpdate.add(qt);
                        }
                    }
                    getQueryTemplates().clear();
                    getQueryTemplates().addAll(toUpdate);
                } else {
                    toUpdate = Lists.newArrayList(getQueryTemplates());
                }

                // Performing a search has the effect of setting the given query as the current active query.
                updateDataNavigationWindow(toUpdate, queriesToRemove);

                // Call our method to perform search with saved template
                doSubmitDiskResourceQuery(new SubmitDiskResourceQueryEvent(queryTemplate));
            }
        });

    }

    /**
     * This handler is responsible for submitting a search with the {@link DiskResourceQueryTemplate}
     * contained in the given {@link SubmitDiskResourceQueryEvent}.
     * <p/>
     * Additionally, this method also ensures that this presenter's query template collection is also maintained in the
     * {@link DiskResourceView#getTreeStore()}, and is responsible for setting the current "active query".
     */
    @Override
    public void doSubmitDiskResourceQuery(final SubmitDiskResourceQueryEvent event) {
        DiskResourceQueryTemplate toSubmit = event.getQueryTemplate();

        activeQuery = toSubmit;
        fireEvent(new FolderSelectedEvent(activeQuery));
    }

    @Override
    public DiskResourceQueryTemplate getActiveQuery() {
        return activeQuery;
    }
    
    @Override
    public void loadSavedQueries(List<DiskResourceQueryTemplate> savedQueries) {
        setCleanCopyQueryTemplates(searchService.createFrozenList(savedQueries));

        List<DiskResourceQueryTemplate> queriesToRemove = Lists.newArrayList(queryTemplates);
        queryTemplates.clear();
        queryTemplates.addAll(savedQueries);

        // Update navigation window
        updateDataNavigationWindow(queryTemplates, queriesToRemove);
    }

    @Override
    public void onDeleteSavedSearch(DeleteSavedSearchEvent event) {
        searchField.clearSearch();
        final DiskResourceQueryTemplate savedSearch = event.getSavedSearch();
        if (queryTemplates.remove(savedSearch)) {
            announcer.schedule(new SuccessAnnouncementConfig("Successfully deleted saved search: "
                    + savedSearch.getName()));
            searchService.saveQueryTemplates(queryTemplates,
                    new AsyncCallback<List<DiskResourceQueryTemplate>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            announcer.schedule(new ErrorAnnouncementConfig("Unable to save filter."));
                        }

                        @Override
                        public void onSuccess(List<DiskResourceQueryTemplate> savedTemplates) {
                            if (queryTemplates.size() != savedTemplates.size()) {
                                GWT.log("Failed to save query templates after delete of saved search");
                            }
                        }
                    });
        } else {
            GWT.log("Failed to remove saved search from presenter");
        }
    }

    @Override
    public void onFolderSelected(FolderSelectedEvent event) {
        if (event.getSelectedFolder() instanceof DiskResourceQueryTemplate) {
            final DiskResourceQueryTemplate selectedQuery = (DiskResourceQueryTemplate)event.getSelectedFolder();
            searchField.edit(selectedQuery);
        } else {
            // Clear search form
            searchField.clearSearch();
        }
    }

    @Override
    public void searchInit(
            final FolderSelectedEvent.HasFolderSelectedEventHandlers hasFolderSelectedHandlers,
            final HasDeleteSavedSearchEventHandlers hasDeleteSavedSearchEventHandlers,
            final FolderSelectedEvent.FolderSelectedEventHandler folderSelectedEventHandler,
            final DiskResourceSearchField searchField) {
        hasFolderSelectedHandlers.addFolderSelectedEventHandler(this);
        hasDeleteSavedSearchEventHandlers.addDeleteSavedSearchEventHandler(this);
        // Add handler which will listen to our FolderSelectedEvents
        addFolderSelectedEventHandler(folderSelectedEventHandler);
        this.searchField = searchField;
        searchField.addSaveDiskResourceQueryEventHandler(this);
        searchField.addSubmitDiskResourceQueryEventHandler(this);
    }

    boolean areTemplatesEqual(DiskResourceQueryTemplate lhs, DiskResourceQueryTemplate rhs) {
        final AutoBean<DiskResourceQueryTemplate> lhsAb = AutoBeanUtils.getAutoBean(lhs);
        final AutoBean<DiskResourceQueryTemplate> rhsAb = AutoBeanUtils.getAutoBean(rhs);

        final boolean deepEquals = AutoBeanUtils.deepEquals(lhsAb, rhsAb);
        return deepEquals;
    }

    HandlerManager createHandlerManager() {
        return new HandlerManager(this);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
    }


    void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    HandlerManager getHandlerManager() {
        return handlerManager;
    }

    List<DiskResourceQueryTemplate> getQueryTemplates() {
        return queryTemplates;
    }

    Set<String> getUniqueNames(List<DiskResourceQueryTemplate> hasNames) {
        final HashSet<String> queryNameSet = Sets.newHashSet();
        for (HasName hasName : hasNames) {
            if (queryNameSet.contains(hasName.getName())) {
                // We have a dupe name!!
                GWT.log("Duplicate QueryTemplate name found: " + hasName.getName());
            } else {
                queryNameSet.add(hasName.getName());
            }
        }

        return queryNameSet;
    }

    void setCleanCopyQueryTemplates(List<DiskResourceQueryTemplate> cleanCopyQueryTemplates) {
        this.cleanCopyQueryTemplates = cleanCopyQueryTemplates;
    }

    /**
     * Fires an {@link UpdateSavedSearchesEvent} with the given {@link DiskResourceQueryTemplate} lists.
     * 
     * @param queryTemplates
     * @param queriesToRemove
     */
    void updateDataNavigationWindow(final List<DiskResourceQueryTemplate> queryTemplates,
            final List<DiskResourceQueryTemplate> queriesToRemove) {
        eventBus.fireEvent(new UpdateSavedSearchesEvent(queryTemplates, queriesToRemove));
    }

    private boolean templateHasChanged(DiskResourceQueryTemplate template, List<DiskResourceQueryTemplate> controlList) {
        for (DiskResourceQueryTemplate qt : controlList) {
            if (qt.getName().equalsIgnoreCase(template.getName()) && !areTemplatesEqual(qt, template)) {
                // Given template has been changed
                return true;
            }
        }
        return false;
    }

    @Override
    public void refreshQuery() {
        DiskResourceQueryTemplate query = getActiveQuery();
        if (query != null) {
            searchField.updateSearch(query);
        }
    }

}
