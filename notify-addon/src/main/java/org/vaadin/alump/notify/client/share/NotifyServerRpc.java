package org.vaadin.alump.notify.client.share;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

// ServerRpc is used to pass events from client to server
public interface NotifyServerRpc extends ServerRpc {

    /**
     * Called when state changes on client side
     * @param state New status
     */
    @Delayed(lastOnly = true)
    void clientStateUpdate(NotifyState state);
}
