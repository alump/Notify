package org.vaadin.alump.notify.client.util;

import org.vaadin.alump.notify.client.share.NotifyState;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alump on 21/03/2017.
 */
public class NotifyUtil {

    private transient final static Logger LOGGER = Logger.getLogger(NotifyUtil.class.getName());

    private static NotifyState state = NotifyState.UNINITIALIZED;

    private static List<ClientNotifyStateListener> listeners = new ArrayList<>();

    private static void noSupport() {
        LOGGER.warning("Notification API not supported");
        setState(NotifyState.NOT_SUPPORTED);
    }

    private static void permissionAsked() {
        if(state == NotifyState.UNINITIALIZED) {
            setState(NotifyState.PERMISSION_ASKED);
        }
    }

    private static void permissionGranted() {
        LOGGER.info("notification permission granted");
        setState(NotifyState.READY);
    }

    private static void permissionDenied() {
        LOGGER.warning("notification permission denied");
        setState(NotifyState.PERMISSION_DENIED);
    }

    public static void setup(ClientNotifyStateListener listener) {
        addListener(listener);
        if(state != NotifyState.UNINITIALIZED) {
            listener.onNewClientNotifyState(state);
        } else {
            initialize();
        }
    }

    public static void addListener(ClientNotifyStateListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ClientNotifyStateListener listener) {
        listeners.remove(listener);
    }

    private static void setState(NotifyState newStatus) {
        if(state != newStatus) {
            state = newStatus;
            listeners.forEach(l -> l.onNewClientNotifyState(newStatus));
        }
    }

    private native static void initialize()
    /*-{
        if (!("Notification" in $wnd)) {
            @org.vaadin.alump.notify.client.util.NotifyUtil::noSupport()();
        } else if ($wnd.Notification.permission === "granted") {
            @org.vaadin.alump.notify.client.util.NotifyUtil::permissionGranted()();
        } else {
            $wnd.Notification.requestPermission(function (permission) {
                if (permission === "granted") {
                    @org.vaadin.alump.notify.client.util.NotifyUtil::permissionGranted()();
                } else {
                    @org.vaadin.alump.notify.client.util.NotifyUtil::permissionDenied()();
                }
            });
            @org.vaadin.alump.notify.client.util.NotifyUtil::permissionAsked()();
        }
     }-*/;

    public static void askPermission() {
        if(state == NotifyState.UNINITIALIZED) {
            initialize();
        }
    }

     public static void show(String title, String body, String icon) {
        if(state == NotifyState.READY) {
            nativeShow(title, body, icon);
        } else {
            LOGGER.severe("API not ready, can not show notification");
        }
     }

    private native static void nativeShow(String titleStr, String bodyStr, String iconStr)
    /*-{
        var options = {
            body: bodyStr,
            icon: iconStr
        };
        var n = new $wnd.Notification(titleStr,options);
    }-*/;
}
