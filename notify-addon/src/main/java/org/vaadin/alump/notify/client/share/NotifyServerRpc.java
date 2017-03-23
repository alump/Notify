package org.vaadin.alump.notify.client.share;

import com.vaadin.shared.communication.ServerRpc;

// ServerRpc is used to pass events from client to server
public interface NotifyServerRpc extends ServerRpc {

    /**
     * Called when state changes on client side
     * @param state New status
     */
    void onClientStateUpdate(NotifyState state);

    /**
     * Called when notification has been handled and it's resources can be released
     * @param id ID of notification handled
     */
    void onNotificationHandled(int id);

    /**
     * Called when notification is clicked
     * @param id ID of notification clicked
     */
    void onNotificationClicked(int id);
}
