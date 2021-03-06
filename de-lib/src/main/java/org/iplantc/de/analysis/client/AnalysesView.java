package org.iplantc.de.analysis.client;


import org.iplantc.de.client.models.AppTypeFilter;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisPermissionFilter;
import org.iplantc.de.client.services.callbacks.ReactErrorCallback;
import org.iplantc.de.client.services.callbacks.ReactSuccessCallback;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

/**
 * @author sriram, jstroot
 */
@JsType
public interface AnalysesView extends IsWidget {

    interface Appearance {
        int windowMinWidth();

        int windowMinHeight();

        String windowWidth();

        String windowHeight();
    }

    @JsType
    interface Presenter {

        interface Appearance {

            String analysesRetrievalFailure();

            SafeHtml analysisCommentUpdateFailed();

            SafeHtml analysisCommentUpdateSuccess();

            SafeHtml analysisRenameFailed();

            SafeHtml analysisRenameSuccess();

            String analysisStopSuccess(String name);

            String comments();

            String deleteAnalysisError();

            String stopAnalysisError(String name);

            String analysisStepInfoError();

            String userRequestingHelpSubject();

            String requestProcessing();

            String commentsDialogWidth();

            String commentsDialogHeight();

            String warning();

            String analysesExecDeleteWarning();

            String rename();

            String renameAnalysis();

            String supportRequestFailed();

            String supportRequestSuccess();

            String htAnalysisTitle(String analysisName);

            String newTimeLimitSuccess(String analysisName);
        }

        @JsIgnore
        void go(final HasOneWidget container,
                String baseDebugId,
                final List<Analysis> selectedAnalyses);

        void setViewDebugId(String baseId);

        @JsIgnore
        AnalysisPermissionFilter getCurrentPermFilter();

        @JsIgnore
        AppTypeFilter getCurrentTypeFilter();

        @SuppressWarnings("unusable-by-js")
        void getAnalyses(int limit,
                         int offset,
                         Splittable filters,
                         String sortField,
                         String sortDir,
                         ReactSuccessCallback callback,
                         ReactErrorCallback errorCallback);

        void renameAnalysis(String analysisId,
                            String newName,
                            ReactSuccessCallback callback,
                            ReactErrorCallback errorCallback);

        void updateAnalysisComments(String id,
                                    String comment,
                                    ReactSuccessCallback callback,
                                    ReactErrorCallback errorCallback);

        void onAnalysisNameSelected(String resultFolderId);

        void onAnalysisAppSelected(String analysisId, String systemId, String appId);

        void onCancelAnalysisSelected(String analysisId,
                                      String analysisName,
                                      ReactSuccessCallback callback,
                                      ReactErrorCallback errorCallback);

        @SuppressWarnings("unusable-by-js")
        void onShareAnalysisSelected(Splittable[] analysisList);

        void deleteAnalyses(String[] analysesToDelete,
                            ReactSuccessCallback callback,
                            ReactErrorCallback errorCallback);

        @SuppressWarnings("unusable-by-js")
        void onUserSupportRequested(Splittable analysis,
                                    String comment,
                                    ReactSuccessCallback callback,
                                    ReactErrorCallback errorCallback);

        void onAnalysisJobInfoSelected(String id,
                                       ReactSuccessCallback callback,
                                       ReactErrorCallback errorCallback);

        void onCompleteAnalysisSelected(String analysisId,
                                        String analysisName,
                                        ReactSuccessCallback callback,
                                        ReactErrorCallback errorCallback);


        void handleViewAndTypeFilterChange(String permFilter,
                                           String appTypeFilter);

        void getVICELogs(String id,
                         String analysisName);

        void closeViceLogsViewer();

        void onFollowViceLogs(boolean follow);

        void refreshViceLogs();

        void handleBatchIconClick(String parentId, String analysisName);

        void handleSearch(String searchTerm);

        void handleViewAllIconClick();

        void getViceTimeLimit(String id,
                              ReactSuccessCallback callback,
                              ReactErrorCallback errorCallback);

        void extendViceTimeLimit(String id,
                                 String analysisName,
                                 ReactSuccessCallback callback,
                                 ReactErrorCallback errorCallback);

    }

    @JsIgnore
    void load(Presenter presenter,
              String baseDebugId,
              Analysis selectedAnalysis);

    void updateFilter(String viewFilter,
                      String appTypeFilter,
                      String nameFilter,
                      String appNameFilter,
                      String idFilter,
                      String parentId);
}
