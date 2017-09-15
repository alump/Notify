package org.vaadin.alump.notify.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.server.Page;
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

    private static List<NotifyUtilListener> listeners = new ArrayList<>();

    private static List<ClientNotification> queue = new ArrayList<>();

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

        for (ClientNotification n : queue) {
            show(n);
        }

        queue.clear();
    }

    private static void onNotificationClicked(int id) {
        for (NotifyUtilListener listener : listeners) {
            listener.onNotificationClicked(id);
        }
    }

    private static void permissionDenied() {
        LOGGER.warning("notification permission denied");
        setState(NotifyState.PERMISSION_DENIED);
        queue.clear();
    }

    public static void setup(NotifyUtilListener listener) {
        addListener(listener);
        if(state != NotifyState.UNINITIALIZED) {
            listener.onNewClientNotifyState(state);
        } else {
            initialize();
        }
    }

    public static void addListener(NotifyUtilListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(NotifyUtilListener listener) {
        listeners.remove(listener);
    }

    private static void setState(NotifyState newStatus) {
        if(state != newStatus) {
            state = newStatus;
            for (NotifyUtilListener listener : listeners) {
                listener.onNewClientNotifyState(newStatus);
            }
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

    private static void onNotificationClosed(int id) {
        fireNotificationHandled(id);
    }

     public static void show(ClientNotification notification) {
        if(state == NotifyState.READY) {
            JavaScriptObject options = createOptions(notification.getBody(), notification.getIconUrl(),
                    notification.getSoundUrl(), notification.getTimeoutMs());
            nativeShow(
                    notification.getId(),
                    notification.getTitle(),
                    options,
                    getClickFlag(notification),
                    notification.getTimeoutMs());

        } else if(state == NotifyState.PERMISSION_ASKED || state == NotifyState.UNINITIALIZED) {
            queue.add(notification);
            LOGGER.info("Permission not granted, queueing message");
            if(state == NotifyState.UNINITIALIZED) {
                askPermission();
            }
        } else {
            LOGGER.severe("API not ready, can not show notification");
            fireNotificationHandled(notification);
        }
     }

     private static int getClickFlag(ClientNotification notification) {
        if(notification.isCloseOnClick()) {
            return 2;
        } else if(notification.isClickable()) {
            return 1;
        } else {
            return 0;
        }
     }

     private native static JavaScriptObject createOptions(String bodyStr, String iconUrl, String soundUrl, Integer timeoutMs)
     /*-{
        var properties = {};

        if(bodyStr) {
            properties.body = bodyStr;
        }
        if(iconUrl) {
            properties.icon = iconUrl;
        }
        if(soundUrl) {
            properties.sound = soundUrl;
        }
        if(timeoutMs) {
            properties.requireInteraction = true;
        }

        return properties;
     }-*/;

     private native static void nativeShow(int id, String titleStr, JavaScriptObject options, int clickable, Integer timeoutMs)
     /*-{
        var n = new $wnd.Notification(titleStr,options);
        if(clickable > 0) {
            n.onclick = function(event) {
                event.preventDefault();
                @org.vaadin.alump.notify.client.util.NotifyUtil::onNotificationClicked(*)(id);
                if(clickable == 2) {
                    n.close();
                }
            };
        }
        if(timeoutMs) {
            setTimeout(n.close.bind(n), timeoutMs);
        }
    }-*/;

    private static void fireNotificationHandled(ClientNotification notification) {
        fireNotificationHandled(notification.getId());
    }

    private static void fireNotificationHandled(int id) {
        for (NotifyUtilListener listener : listeners) {
            listener.onNotificationHandled(id);
        }
    }
}
