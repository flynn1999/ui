package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsListView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.CommunitiesView;
import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.client.WorkspaceView;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.RefreshAppsSelectedEvent;
import org.iplantc.de.apps.client.gin.factory.AppsViewFactory;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.groups.Group;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.widgets.DETabPanel;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * The presenter for the AppsView.
 *
 * @author jstroot
 */
public class AppsViewPresenterImpl implements AppsView.Presenter,
                                              RefreshAppsSelectedEvent.RefreshAppsSelectedEventHandler {

    protected final AppsView view;
    private final AppCategoriesView.Presenter categoriesPresenter;
    private final AppsListView.Presenter appsListPresenter;
    private final OntologyHierarchiesView.Presenter hierarchiesPresenter;
    private final CommunitiesView.Presenter communitiesPresenter;
    private final AppsToolbarView.Presenter toolbarPresenter;
    @Inject UserInfo userInfo;
    OntologyUtil ontologyUtil;
    private WorkspaceView.Presenter workspacePresenter;

    @Inject
    protected AppsViewPresenterImpl(final AppsViewFactory viewFactory,
                                    final AppsListView.Presenter appsListPresenter,
                                    final AppsToolbarView.Presenter toolbarPresenter,
                                    final OntologyHierarchiesView.Presenter hierarchiesPresenter,
                                    final WorkspaceView.Presenter workspacePresenter) {
        this.workspacePresenter = workspacePresenter;
        this.categoriesPresenter = workspacePresenter.getCategoriesPresenter();
        this.appsListPresenter = appsListPresenter;
        this.hierarchiesPresenter = hierarchiesPresenter;
        this.toolbarPresenter = toolbarPresenter;
        this.communitiesPresenter = workspacePresenter.getCommunitiesPresenter();
        this.view = viewFactory.create(categoriesPresenter,
                                       communitiesPresenter,
                                       hierarchiesPresenter,
                                       appsListPresenter,
                                       toolbarPresenter);
        this.ontologyUtil = OntologyUtil.getInstance();

        categoriesPresenter.addAppCategorySelectedEventHandler(appsListPresenter);
        categoriesPresenter.addAppCategorySelectedEventHandler(toolbarPresenter.getView());
        categoriesPresenter.addAppCategorySelectedEventHandler(communitiesPresenter);

        hierarchiesPresenter.addOntologyHierarchySelectionChangedEventHandler(appsListPresenter);
        hierarchiesPresenter.addOntologyHierarchySelectionChangedEventHandler(toolbarPresenter.getView());
        hierarchiesPresenter.addSelectedHierarchyNotFoundHandler(categoriesPresenter);

        communitiesPresenter.addCommunitySelectedEventHandler(appsListPresenter);
        communitiesPresenter.addCommunitySelectedEventHandler(toolbarPresenter.getView());
        communitiesPresenter.addCommunitySelectedEventHandler(categoriesPresenter);

        appsListPresenter.addAppSelectionChangedEventHandler(toolbarPresenter.getView());
        appsListPresenter.addAppInfoSelectedEventHandler(hierarchiesPresenter);

        toolbarPresenter.getView().addDeleteAppsSelectedHandler(appsListPresenter);
        toolbarPresenter.getView().addCopyAppSelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addCopyWorkflowSelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addRunAppSelectedHandler(appsListPresenter);
        toolbarPresenter.getView().addBeforeAppSearchEventHandler(appsListPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(communitiesPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(categoriesPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(hierarchiesPresenter);
        toolbarPresenter.getView().addAppSearchResultLoadEventHandler(appsListPresenter);
        toolbarPresenter.getView().addSwapViewButtonClickedEventHandler(appsListPresenter);
        toolbarPresenter.getView().addRefreshAppsSelectedEventHandler(this);
        toolbarPresenter.getView().addManageToolsClickedEventHandler(toolbarPresenter);
    }

    @Override
    public App getSelectedApp() {
        return appsListPresenter.getSelectedApp();
    }

    @Override
    public AppCategory getSelectedAppCategory() {
        return categoriesPresenter.getSelectedAppCategory();
    }

    @Override
    public void go(final HasOneWidget container,
                   final HasId selectedAppCategory,
                   final HasId selectedApp, final String activeView, final boolean catPanelCollapsed) {
        DETabPanel tabPanel = view.getCategoryTabPanel();
        if (isEmpty(tabPanel)) {
            workspacePresenter.go(selectedAppCategory, null, true, tabPanel);
            hierarchiesPresenter.go(null, tabPanel);
        }
        if (!Strings.isNullOrEmpty(activeView)) {
            appsListPresenter.setActiveView(activeView);
        }
        container.setWidget(view);
        view.setNavPanelCollapsed(catPanelCollapsed);
    }

    boolean isEmpty(TabPanel tabPanel) {
        return tabPanel.getWidgetCount() == 0;
    }

    @Override
    public AppsView.Presenter hideAppMenu() {
        view.hideAppMenu();
        return this;
    }

    @Override
    public AppsView.Presenter hideWorkflowMenu() {
        view.hideWorkflowMenu();
        return this;
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
        workspacePresenter.setViewDebugId(baseId);
    }

    @Override
    public void checkForAgaveRedirect() {
        categoriesPresenter.checkForAgaveRedirect();
    }


    /**
     * Get current active view
     *
     * @return the active view
     */
    @Override
    public String getActiveView() {
        return appsListPresenter.getActiveView();
    }

    /**
     * Set new active view
     *
     * @param activeView view to set as active
     */
    @Override
    public void setActiveView(String activeView) {
        appsListPresenter.setActiveView(activeView);
    }

    @Override
    public void onRefreshAppsSelected(RefreshAppsSelectedEvent event) {
        AppCategory selectedAppCategory = categoriesPresenter.getSelectedAppCategory();
        OntologyHierarchy selectedHierarchy = hierarchiesPresenter.getSelectedHierarchy();
        Group selectedCommunity = communitiesPresenter.getSelectedCommunity();
        boolean hasSearchPhrase = toolbarPresenter.getView().hasSearchPhrase();
        boolean useDefaultSelection = !hasSearchPhrase && selectedHierarchy == null && selectedCommunity == null;

        view.clearTabPanel();
        DETabPanel categoryTabPanel = view.getCategoryTabPanel();

        workspacePresenter.go(selectedAppCategory, selectedCommunity, useDefaultSelection, categoryTabPanel);
        hierarchiesPresenter.go(selectedHierarchy, categoryTabPanel);
        if (hasSearchPhrase) {
            toolbarPresenter.reloadSearchResults();
        }
    }

    @Override
    public String getWestPanelWidth() {
        return view.getWestPanelWidth();
    }

    @Override
    public void setWestPanelWidth(String width) {
        view.setWestPanelWidth(width);
    }

    @Override
    public boolean isDetailsCollapsed() {
        return view.isNavPanelCollapsed();
    }

    @Override
    public void addAppSelectionChangedHandler(AppSelectionChangedEvent.AppSelectionChangedEventHandler handler) {
        appsListPresenter.addAppSelectionChangedEventHandler(handler);
    }

    @Override
    public void setSingleAppSelection(boolean singleAppSelection) {
        appsListPresenter.setSingleAppSelection(singleAppSelection);
    }
}
