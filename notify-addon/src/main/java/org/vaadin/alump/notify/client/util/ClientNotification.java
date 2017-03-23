package org.vaadin.alump.notify.client.util;

import org.vaadin.alump.notify.client.NotifyConnector;
import org.vaadin.alump.notify.client.share.SharedNotification;

/**
 * Client version of notification. Some values have been converted from Notification reserved from server.
 */
public class ClientNotification {
    private final int id;
    private final String title;
    private final String body;
    private final String iconUrl;
    private final String soundUrl;
    private final boolean clickable;
    private final Integer timeoutMs;
    private final boolean closeOnClick;

    public ClientNotification(NotifyConnector connector, SharedNotification shared) {
        id = shared.id;
        title = shared.title;
        body = shared.body;
        if(shared.iconRes != null) {
            iconUrl = connector.getResourceUrl(shared.iconRes);
        } else {
            iconUrl = null;
        }
        if(shared.soundRes != null) {
            soundUrl = connector.getResourceUrl(shared.soundRes);
        } else {
            soundUrl = null;
        }
        clickable = shared.hasClickListener;
        timeoutMs = shared.timeoutMs == null ? connector.getState().defaultTimeoutMs : shared.timeoutMs;
        closeOnClick = connector.getState().closeOnClick;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getSoundUrl() {
        return iconUrl;
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }
}
