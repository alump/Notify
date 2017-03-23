package org.vaadin.alump.notify.client;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.ServerRpcQueue;
import com.vaadin.client.extensions.AbstractExtensionConnector;

import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.notify.Notify;
import org.vaadin.alump.notify.client.share.*;
import org.vaadin.alump.notify.client.util.ClientNotification;
import org.vaadin.alump.notify.client.util.NotifyUtilListener;
import org.vaadin.alump.notify.client.util.NotifyUtil;

/**
 * Connector for Notify extension class
 */
@Connect(Notify.class)
public class NotifyConnector extends AbstractExtensionConnector implements NotifyUtilListener {

    private NotifyClientRpc clientRpc = new NotifyClientRpc() {
        @Override
        public void showNotification(SharedNotification notification) {
            NotifyUtil.show(new ClientNotification(NotifyConnector.this, notification));
        }

        @Override
        public void askPermission() {
            NotifyUtil.askPermission();
        }
    };

    @Override
    public void init() {
        super.init();
        registerRpc(NotifyClientRpc.class, clientRpc);
        NotifyUtil.addListener(this);
    }

    @Override
    public void onUnregister() {
        NotifyUtil.removeListener(this);
        super.onUnregister();
    }

    @Override
    protected void extend(ServerConnector serverConnector) {
        //empty for now
    }

    @Override
    public NotifySharedState getState() {
        return (NotifySharedState)super.getState();
    }


    @Override
    public void onNewClientNotifyState(NotifyState state) {
        getRpcProxy(NotifyServerRpc.class).onClientStateUpdate(state);
    }

    @Override
    public void onNotificationHandled(int id) {
        getRpcProxy(NotifyServerRpc.class).onNotificationHandled(id);
    }

    @Override
    public void onNotificationClicked(int id) {
        debug("Calling rpc proxy");
        getRpcProxy(NotifyServerRpc.class).onNotificationClicked(id);
        // Calling flush here as optimization might delay call as user is clicking outside browser window
        getConnection().getMessageSender().sendInvocationsToServer();
    }

    private static native void debug(String message)
    /*-{
        console.log(message);
    }-*/;
}
