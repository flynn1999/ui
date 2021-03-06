package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.DELETE;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.services.QuickLaunchServiceFacade;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.services.converters.SplittableDECallbackConverter;
import org.iplantc.de.client.util.AnalysisSubmissionUtil;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.shared.DECallback;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import javax.inject.Inject;

/**
 *
 * @author sriram 
 *
 */
public class QuickLaunchServiceFacadeImpl implements QuickLaunchServiceFacade {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String IS_PUBLIC = "is_public";
    public static final String APP_ID = "app_id";
    private final String QUICK_LAUNCH = "org.iplantc.services.quickLaunches";
    private final AppTemplateUtils appTemplateUtils;
    private final DiscEnvApiService deServiceFacade;
    private final AppTemplateAutoBeanFactory factory;

    @Inject
    public QuickLaunchServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                        final AppTemplateAutoBeanFactory factory,
                                        final AppTemplateUtils appTemplateUtils) {
        this.deServiceFacade = deServiceFacade;
        this.factory = factory;
        this.appTemplateUtils = appTemplateUtils;
    }


    @Override
    public void createQuickLaunch(String name,
                                  String description,
                                  boolean isPublic,
                                  AppTemplate at,
                                  JobExecution je,
                                  DECallback<Splittable> callback) {
        String address = QUICK_LAUNCH;
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(name).assign(body, NAME);
        StringQuoter.create(description).assign(body, DESCRIPTION);
        StringQuoter.create(isPublic).assign(body, IS_PUBLIC);
        StringQuoter.create(at.getId()).assign(body, APP_ID);
        Splittable assembledPayload =
                AnalysisSubmissionUtil.assembleLaunchAnalysisPayload(appTemplateUtils, at, je);

        assembledPayload.assign(body, "submission");
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.getPayload());
        deServiceFacade.getServiceData(wrapper, new SplittableDECallbackConverter(callback));
    }

    @Override
    public void listQuickLaunches(String appId,
                                  DECallback<Splittable> callback) {
        String address = QUICK_LAUNCH + "/apps/" + appId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new SplittableDECallbackConverter(callback));

    }

    @Override
    public void deleteQuickLaunch(String qid,
                                  DECallback<Splittable> callback) {
        String address = QUICK_LAUNCH + "/" + qid;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address, "{}");
        deServiceFacade.getServiceData(wrapper, new SplittableDECallbackConverter(callback));
    }

    @Override
    public void reLaunchInfo(String qid,
                             DECallback<AppTemplate> callback) {
        String address = QUICK_LAUNCH + "/" + qid + "/app-info";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(factory, callback));
    }
}
