package org.vaadin.alump.notify;

import com.vaadin.server.AbstractExtension;

import com.vaadin.server.Resource;
import com.vaadin.ui.UI;
import org.vaadin.alump.notify.client.share.*;
import org.vaadin.alump.notify.exceptions.NotifyUINotResolvedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Notify addon adds easy server side Java API to access Notification API on browser
 */
public class Notify extends AbstractExtension {

    private NotifyState clientState = NotifyState.UNINITIALIZED;
    private int resourceIndex = 0;
    private final List<NotifyStateListener> stateListeners;

    private final NotifyServerRpc serverRpc = new NotifyServerRpc() {

        @Override
        public void clientStateUpdate(NotifyState newState) {
            NotifyState oldState = newState;
            Notify.this.clientState = newState;
            NotifyStateEvent event = new NotifyStateEvent(Notify.this.getUI(), oldState, newState);
            stateListeners.forEach(l -> l.onNotifyStateChange(event));
        }
    };

    protected Notify() {
        stateListeners = new ArrayList<>();
        registerRpc(serverRpc, NotifyServerRpc.class);
    }

    protected Notify(UI ui) {
        this();
        extend(ui);
    }

    @Override
    protected NotifySharedState getState() {
        return (NotifySharedState) super.getState();
    }

    public boolean isReady() {
        return clientState == NotifyState.READY;
    }

    protected static Notify getInstance(UI ui) {
        return (Notify)ui.getExtensions().stream().filter(e -> e instanceof Notify).findFirst()
                .orElseGet(() -> new Notify(ui));
    }

    protected void instanceAskPermission() {
        getRpcProxy(NotifyClientRpc.class).askPermission();
    }

    protected void instanceShow(String title, String body, Resource icon) {
        String resourceKey = null;
        if(icon != null) {
            resourceKey = getNextResourceKey();
            setResource(resourceKey, icon);
        }

        NotifyNotification notification = new NotifyNotification(title, body, resourceKey);
        getRpcProxy(NotifyClientRpc.class).showNotification(notification);
    }

    protected String getNextResourceKey() {
        return "res-" + (++resourceIndex);
    }

    public static void askPermission() throws NotifyUINotResolvedException {
        askPermission(resolveUI());
    }

    public static void askPermission(UI ui) {
        getInstance(ui).instanceAskPermission();
    }

    public static void show(String title, String body, Resource icon) throws NotifyUINotResolvedException {
        show(resolveUI(), title, body, icon);
    }

    public static void show(UI ui, String title, String body, Resource icon) {
        getInstance(ui).instanceShow(title, body, icon);
    }

    protected void instanceAddListener(NotifyStateListener listener) {
        stateListeners.add(listener);
    }

    protected void instanceRemoveListener(NotifyStateListener listener) {
        stateListeners.remove(listener);
    }

    public static void addStateListener(NotifyStateListener listener) throws NotifyUINotResolvedException {
        addStateListener(resolveUI(), listener);
    }

    public static void addStateListener(UI ui, NotifyStateListener listener) {
        getInstance(ui).instanceAddListener(listener);
    }

    public static void removeStateListener(NotifyStateListener listener) throws NotifyUINotResolvedException {
        removeStateListener(resolveUI(), listener);
    }

    public static void removeStateListener(UI ui, NotifyStateListener listener) {
        getInstance(ui).instanceRemoveListener(listener);
    }

    protected NotifyState instanceGetState() {
        return clientState;
    }

    public static NotifyState getClientState(UI ui) {
        return getInstance(ui).clientState;
    }

    public static NotifyState getClientState() {
        return getClientState(resolveUI());
    }

    private static UI resolveUI() throws NotifyUINotResolvedException {
        return Optional.ofNullable(UI.getCurrent()).orElseThrow(() -> new NotifyUINotResolvedException());
    }

}
