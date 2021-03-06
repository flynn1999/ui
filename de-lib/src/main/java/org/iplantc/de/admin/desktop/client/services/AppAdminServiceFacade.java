package org.iplantc.de.admin.desktop.client.services;

import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.HasQualifiedId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.services.AppSearchFacade;
import org.iplantc.de.shared.DECallback;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppAdminServiceFacade extends  AppSearchFacade {

    interface AdminServiceAutoBeanFactory extends AutoBeanFactory {
        AutoBean<AppCategory> appCategory();
        AutoBean<App> app();
        AutoBean<AppDoc> appDoc();
    }

    /**
     * Adds a new Category with the given category name.
     */
    void addCategory(String systemId,
                     String newCategoryName,
                     HasQualifiedId parentCategory,
                     AsyncCallback<AppCategory> callback);

    void getPublicAppCategories(DECallback<List<AppCategory>> asyncCallback, boolean loadHpc);

    /**
     * Renames a Category with the given category ID to the given name.
     */
    void renameAppCategory(HasQualifiedId categoryId,
                           String newCategoryName,
                           AsyncCallback<AppCategory> callback);

    /**
     * Moves a Category with the given category ID to a parent Category with the given parentCategoryId.
     */
    void moveCategory(HasQualifiedId category, HasQualifiedId parent, AsyncCallback<String> callback);

    /**
     * Deletes the Category with the given category ID.
     */
    void deleteAppCategory(HasQualifiedId category, AsyncCallback<Void> callback);

    /**
     * Updates an app with the given values in application.
     *
     */
    void restoreApp(HasQualifiedId app,
                    AsyncCallback<App> callback);

    void updateApp(App app, AsyncCallback<App> callback);

    /**
     * Deletes an App with the given applicationId.
     */
    void deleteApp(HasQualifiedId app, AsyncCallback<Void> callback);

    void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback);

    void getAppDetails(final String appId,
                       final String systemId,
                       final AsyncCallback<Splittable> callback);

    void getAppDoc(HasQualifiedId app, AsyncCallback<AppDoc> callback);

    void saveAppDoc(HasQualifiedId app, String doc, AsyncCallback<AppDoc> callback);

    void updateAppDoc(HasQualifiedId app, String doc, AsyncCallback<AppDoc> callback);

    void getAppPublicationRequests(AsyncCallback<Splittable> callback);

    void publishApp(String appId,
                    String systemId,
                    AsyncCallback<String> callback);

}
