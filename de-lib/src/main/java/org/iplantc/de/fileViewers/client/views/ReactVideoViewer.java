package org.iplantc.de.fileViewers.client.views;

import gwt.react.client.components.ComponentConstructorFn;
import gwt.react.client.proptypes.BaseProps;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * @author aramsey
 */

@JsType(isNative = true, namespace = "CyVerseReactComponents", name = "fileViewers")
public class ReactVideoViewer {

    public static ComponentConstructorFn<VideoViewerProps> VideoViewer;

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    static class VideoViewerProps extends BaseProps {
        public String url;
    }

}
