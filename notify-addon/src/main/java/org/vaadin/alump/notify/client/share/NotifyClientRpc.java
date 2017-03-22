package org.vaadin.alump.notify.client.share;

import com.vaadin.shared.communication.ClientRpc;

public interface NotifyClientRpc extends ClientRpc {

    void showNotification(NotifyNotification notification);

    void askPermission();

}