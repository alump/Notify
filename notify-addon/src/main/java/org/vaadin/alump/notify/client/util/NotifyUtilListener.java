package org.vaadin.alump.notify.client.util;

import org.vaadin.alump.notify.client.share.NotifyState;

/**
 * Listener for NotifyUtil events
 */
public interface NotifyUtilListener {
    void onNewClientNotifyState(NotifyState state);
    void onNotificationHandled(int id);
    void onNotificationClicked(int id);
}
