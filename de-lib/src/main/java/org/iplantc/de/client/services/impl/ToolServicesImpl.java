package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolType;
import org.iplantc.de.client.models.tool.ToolTypeList;
import org.iplantc.de.client.models.tool.sharing.ToolPermissionsRequest;
import org.iplantc.de.client.models.tool.sharing.ToolSharingAutoBeanFactory;
import org.iplantc.de.client.models.tool.sharing.ToolSharingRequestList;
import org.iplantc.de.client.models.tool.sharing.ToolUnSharingRequestList;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.ToolServices;
import org.iplantc.de.client.services.converters.DECallbackConverter;
import org.iplantc.de.client.services.converters.SplittableDECallbackConverter;
import org.iplantc.de.client.services.converters.StringToSplittableDECallbackConverter;
import org.iplantc.de.client.services.converters.ToolCallbackConverter;
import org.iplantc.de.client.services.converters.ToolsCallbackConverter;
import org.iplantc.de.shared.AppsCallback;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

import java.util.ArrayList;
import java.util.List;

public class ToolServicesImpl implements ToolServices {

    private final String TOOLS = "org.iplantc.services.tools";
    private static final String APP_ELEMENTS = "org.iplantc.services.apps.elements";
    private final ToolAutoBeanFactory factory;
    private final DiscEnvApiService deServiceFacade;

    @Inject
    ToolSharingAutoBeanFactory sharingFactory;
    @Inject
    AppServiceFacade.AppServiceAutoBeanFactory svcFactory;

    @Inject
    public ToolServicesImpl(final DiscEnvApiService deServiceFacade, final ToolAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.factory = factory;
    }

    @Override
    public void searchTools(Boolean isPublic, FilterPagingLoadConfig loadConfig, AppsCallback<List<Tool>> callback) {
        String address = TOOLS + "?";
        // Get the proxy's search params.
        String searchTerm = null;
        if(loadConfig != null) {
            List<FilterConfig> filterConfigs = loadConfig.getFilters();
            if (filterConfigs != null && !filterConfigs.isEmpty()) {
                searchTerm = filterConfigs.get(0).getValue();
            }
            if (Strings.isNullOrEmpty(searchTerm)) {
                searchTerm = "*";
            }

            SortInfo sortInfo =
                    Iterables.getFirst(loadConfig.getSortInfo(), new SortInfoBean("NAME", SortDir.ASC));

            address += "search=" + URL.encodeQueryString(searchTerm) + "&sort-field=" + sortInfo.getSortField().toLowerCase()
                       + "&sort-dir=" + sortInfo.getSortDir().toString();
        }

        if(isPublic != null) {
            address += "&public=" + isPublic;
        }

        ToolsCallbackConverter callbackCnvt = new ToolsCallbackConverter(callback, factory);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

        deServiceFacade.getServiceData(wrapper, callbackCnvt);
    }

    @Override
    public void searchTools(Boolean isPublic, String searchTerm, String order, String orderBy, int limit, int offset, AppsCallback<Splittable> callback) {
        String address = TOOLS + "?";

        String search = Strings.isNullOrEmpty(searchTerm) ? "*" : searchTerm;
        address += "search=" + URL.encodeQueryString(search);
        address += "&limit=" + limit;
        address += "&offset=" + offset;
        if(isPublic != null) {
            address += "&public=" + isPublic;
        }
        address += "&sort-field=" + orderBy.toLowerCase();
        address += "&sort-dir=" + order.toUpperCase();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

        deServiceFacade.getServiceData(wrapper, new StringToSplittableDECallbackConverter(callback));
    }

    @Override
    public void addTool(Tool tool, AppsCallback<Splittable> callback) {
        String address = TOOLS;
        String newTool = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(tool)).getPayload();
        ServiceCallWrapper wrapper =
                new ServiceCallWrapper(BaseServiceCallWrapper.Type.POST, address, newTool);

        deServiceFacade.getServiceData(wrapper, new StringToSplittableDECallbackConverter(callback));
    }

    @Override
    public void deleteTool(String toolId, AppsCallback<Void> callback) {
        String address = TOOLS + "/" + toolId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(BaseServiceCallWrapper.Type.DELETE, address);

        deServiceFacade.getServiceData(wrapper, new DECallbackConverter<String, Void>(callback) {
            @Override
            protected Void convertFrom(String object) {
                return null;
            }
        });
    }

    @Override
    public void getPermissions(List<Tool> currentSelection, AppsCallback<String> callback) {
        String address = TOOLS + "/" + "permission-lister";
        List<String> toolPermissionList = new ArrayList<>();

        for (Tool t : currentSelection) {
            toolPermissionList.add(t.getId());
        }

        final AutoBean<ToolPermissionsRequest> requestAutoBean = sharingFactory.ToolPermissionsRequest();
        requestAutoBean.as().setTools(toolPermissionList);
        final Splittable requestJson = AutoBeanCodex.encode(requestAutoBean);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(BaseServiceCallWrapper.Type.POST,
                                                            address,
                                                            requestJson.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void shareTool(ToolSharingRequestList obj, AppsCallback<String> callback) {
        final String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(obj)).getPayload();
        GWT.log("tool sharing request:" + payload);
        String address = TOOLS + "/" + "sharing";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void unShareTool(ToolUnSharingRequestList obj, AppsCallback<String> callback) {
        final String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(obj)).getPayload();
        GWT.log("tool un-sharing request:" + payload);
        String address = TOOLS + "/" + "unsharing";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void updateTool(Tool tool, AppsCallback<Tool> appsCallback) {
        String address = TOOLS + "/" + tool.getId();
        Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(tool));
        Splittable.NULL.assign(encode,"permission");
        Splittable.NULL.assign(encode,"implementation");

        String newTool = encode.getPayload();
        ToolCallbackConverter callbackCnvt = new ToolCallbackConverter(appsCallback, factory);
        ServiceCallWrapper wrapper =
                new ServiceCallWrapper(BaseServiceCallWrapper.Type.PATCH, address, newTool);

        deServiceFacade.getServiceData(wrapper, callbackCnvt);
    }

    @Override
    public void getAppsForTool(String toolId,
                               AppsCallback<Splittable> appsCallback) {
       String address = TOOLS + "/" + toolId + "/apps";

       ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new SplittableDECallbackConverter(appsCallback));
       
    }

    @Override
    public void getToolInfo(String toolId, AppsCallback<Tool> appsCallback) {
        String address = TOOLS + "/" + toolId;
        ToolCallbackConverter callbackCnvt = new ToolCallbackConverter(appsCallback, factory);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(BaseServiceCallWrapper.Type.GET, address);

        deServiceFacade.getServiceData(wrapper, callbackCnvt);
    }

    @Override
    public void getToolTypes(AppsCallback<List<ToolType>> callback) {
        String address = APP_ELEMENTS + "/tool-types";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new DECallbackConverter<String, List<ToolType>>(callback) {
            @Override
            protected List<ToolType> convertFrom(String object) {
                ToolTypeList typeList = AutoBeanCodex.decode(factory, ToolTypeList.class, object).as();
                return typeList.getToolTypes();
            }
        });
    }

}
