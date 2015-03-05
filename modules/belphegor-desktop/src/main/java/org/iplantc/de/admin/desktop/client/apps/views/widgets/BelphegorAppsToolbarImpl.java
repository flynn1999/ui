package org.iplantc.de.admin.desktop.client.apps.views.widgets;

import org.iplantc.de.admin.desktop.client.apps.views.AdminAppsView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent.AppSelectionChangedEventHandler;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import org.iplantc.de.apps.client.views.toolBar.AppSearchField;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.theme.gray.client.toolbar.GrayToolBarAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class BelphegorAppsToolbarImpl implements AdminAppsView.Toolbar,
                                                 AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                                 AppSelectionChangedEventHandler,
                                                 AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

    @UiTemplate("BelphegorAppsViewToolbar.ui.xml")
    interface BelphegorAppsViewToolbarUiBinder extends UiBinder<Widget, BelphegorAppsToolbarImpl> { }

    @UiField TextButton addCategory;
    @UiField AppSearchField appSearch;
    @UiField TextButton categorizeApp;
    @UiField TextButton deleteCat;
    @UiField PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    @UiField TextButton renameCategory;
    @UiField TextButton moveCategory;
    @UiField TextButton restoreApp;
    @UiField TextButton deleteApp;
    @UiField ToolBar toolBar;

    private static BelphegorAppsViewToolbarUiBinder uiBinder = GWT.create(BelphegorAppsViewToolbarUiBinder.class);
    private final AppAutoBeanFactory appFactory;
    private final AppSearchAutoBeanFactory appSearchFactory;
    private final AppServiceFacade appService;
    private final Widget widget;
    private AdminAppsView.AdminPresenter presenter;
    private AppSearchRpcProxy proxy;

    @Inject
    public BelphegorAppsToolbarImpl(final AppServiceFacade appService,
                                    final AppSearchAutoBeanFactory appSearchFactory,
                                    final AppAutoBeanFactory appFactory) {
        this.appService = appService;
        this.appSearchFactory = appSearchFactory;
        this.appFactory = appFactory;
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEvent.AppSearchResultLoadEventHandler handler) {
        return asWidget().addHandler(handler, AppSearchResultLoadEvent.TYPE);
    }

    @UiHandler("addCategory")
    public void addCategoryClicked(SelectEvent event) {
        presenter.onAddAppCategoryClicked();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler("categorizeApp")
    public void categorizeAppClicked(SelectEvent event) {
        presenter.onCategorizeAppClicked();
    }

    @UiHandler("deleteCat")
    public void deleteCatClicked(SelectEvent event) {
        presenter.onDeleteCatClicked();
    }

    @UiHandler("deleteApp")
    public void deleteAppClicked(SelectEvent event) {
        presenter.onDeleteAppClicked();
    }

    @UiHandler("moveCategory")
    public void moveCategory(SelectEvent event) {
        presenter.onMoveCategoryClicked();
    }

    @Override
    public void init(final AdminAppsView.AdminPresenter presenter,
                     final AdminAppsView appView,
                     final HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                     final AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers) {
        this.presenter = presenter;
        // FIXME wire up search result load handler
//        addAppSearchResultLoadEventHandler(appView);
        hasAppSelectionChangedEventHandlers.addAppSelectionChangedEventHandler(this);
        hasAppCategorySelectionChangedEventHandlers.addAppCategorySelectedEventHandler(this);
        proxy.setHasHandlers(asWidget());
        proxy.setMaskable(new IsMaskable() {
            @Override
            public void mask(String loadingMask) {
                appView.maskCenterPanel(loadingMask);
            }

            @Override
            public void unmask() {
                appView.unMaskCenterPanel();
            }
        });
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        final List<AppCategory> appCategorySelection = event.getAppCategorySelection();

        boolean renameCategoryEnabled, deleteEnabled, moveCatEnabled;
        switch (appCategorySelection.size()){
            case 1:
                renameCategoryEnabled = true;
                deleteEnabled = true;
                moveCatEnabled = true;
                break;
            default:
                renameCategoryEnabled = false;
                deleteEnabled = false;
                moveCatEnabled = false;

        }
        addCategory.setEnabled(true);
        renameCategory.setEnabled(renameCategoryEnabled);
        deleteCat.setEnabled(deleteEnabled);
        moveCategory.setEnabled(moveCatEnabled);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        final List<App> appSelection = event.getAppSelection();

        boolean deleteEnabled, restoreAppEnabled, categorizeAppEnabled;
        switch (appSelection.size()){
            case 1:
                final boolean isDeleted = appSelection.get(0).isDeleted();
                deleteEnabled = !isDeleted;
                restoreAppEnabled = isDeleted;
                categorizeAppEnabled = !isDeleted;
                break;
            default:
                deleteEnabled = false;
                restoreAppEnabled = false;
                categorizeAppEnabled = false;

        }
        addCategory.setEnabled(false);
        renameCategory.setEnabled(false);
        deleteApp.setEnabled(deleteEnabled);
        restoreApp.setEnabled(restoreAppEnabled);
        categorizeApp.setEnabled(categorizeAppEnabled);
    }

    @UiHandler("renameCategory")
    public void renameCategoryClicked(SelectEvent event) {
        presenter.onRenameAppCategoryClicked();
    }

    @UiHandler("restoreApp")
    public void restoreAppClicked(SelectEvent event) {
        presenter.onRestoreAppClicked();
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    @UiFactory
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
        IplantDisplayStrings displayStrings = GWT.create(IplantDisplayStrings.class);
        // FIXME Fix this with injection
        AppsToolbarView.AppsToolbarAppearance appearance = GWT.create(AppsToolbarView.AppsToolbarAppearance.class);
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory, appearance);
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<>(
                proxy);

        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

    @UiFactory
    ToolBar createToolbar() {
        return new ToolBar(new GrayToolBarAppearance());
    }
}
