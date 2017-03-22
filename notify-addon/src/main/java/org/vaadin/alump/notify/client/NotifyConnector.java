package org.vaadin.alump.notify.client;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;

import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.notify.Notify;
import org.vaadin.alump.notify.client.share.*;
import org.vaadin.alump.notify.client.util.ClientNotifyStateListener;
import org.vaadin.alump.notify.client.util.NotifyUtil;

/**
 * Connector for Notify extension class
 */
@Connect(Notify.class)
public class NotifyConnector extends AbstractExtensionConnector implements ClientNotifyStateListener {

    private NotifyClientRpc clientRpc = new NotifyClientRpc() {
        @Override
        public void showNotification(NotifyNotification notification) {
            String iconUrl = null;
            if(notification.iconRes != null) {
                iconUrl = NotifyConnector.this.getResourceUrl(notification.iconRes);
            }
            NotifyUtil.show(notification.title, notification.body, iconUrl);
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
        NotifyUtil.setup(this);
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
        getRpcProxy(NotifyServerRpc.class).clientStateUpdate(state);
    }
}
