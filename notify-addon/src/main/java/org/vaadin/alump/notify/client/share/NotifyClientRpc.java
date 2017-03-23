package org.vaadin.alump.notify.client.share;

import com.vaadin.shared.communication.ClientRpc;

public interface NotifyClientRpc extends ClientRpc {

    void showNotification(SharedNotification notification);

    void askPermission();

}