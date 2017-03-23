/**
 * Notify.java (Notify)
 *
 * Copyright 2017 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.alump.notify;

import com.vaadin.server.AbstractExtension;

import com.vaadin.server.Resource;
import com.vaadin.ui.UI;
import org.vaadin.alump.notify.client.share.*;
import org.vaadin.alump.notify.exceptions.NotificationAPINotSupportedException;
import org.vaadin.alump.notify.exceptions.NotificationsDeniedByUserException;
import org.vaadin.alump.notify.exceptions.NotifyRuntimeException;
import org.vaadin.alump.notify.exceptions.NotifyUINotResolvedException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Notify addon adds easy server side Java API to access Notification API on browser
 */
public class Notify extends AbstractExtension {

    private NotifyState clientState = NotifyState.UNINITIALIZED;
    private AtomicInteger notificationCounter = new AtomicInteger(0);
    private final List<NotifyStateListener> stateListeners;
    private final Map<Integer,NotifyItem> pendingNotifications = new HashMap<>();

    private final NotifyServerRpc serverRpc = new NotifyServerRpc() {

        @Override
        public void onClientStateUpdate(NotifyState newState) {
            NotifyState oldState = newState;
            Notify.this.clientState = newState;
            NotifyStateEvent event = new NotifyStateEvent(Notify.this.getUI(), oldState, newState);
            stateListeners.forEach(l -> l.onNotifyStateChange(event));
        }

        @Override
        public void onNotificationHandled(int id) {
            Notify.this.setResource(getIconResourceKey(id), null);
            Notify.this.setResource(getSoundResourceKey(id), null);
            pendingNotifications.remove(id);
        }

        @Override
        public void onNotificationClicked(int id) {
            NotifyItem notification = pendingNotifications.get(id);
            if(notification == null) {
                return;
            }
            final NotifyClickEvent event = new NotifyClickEvent(notification);
            notification.getClickListener().ifPresent(l -> l.onNotificationClick(event));
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

    private boolean instanceIsReady() {
        return clientState == NotifyState.READY;
    }

    protected static Notify getInstance(UI ui) {
        return getInstanceOptional(ui).orElseGet(() -> new Notify(ui));
    }

    protected static Optional<Notify> getInstanceOptional(UI ui) {
        return ui.getExtensions().stream().filter(e -> e instanceof Notify).findFirst().map(e -> (Notify)e);
    }

    protected void instanceAskPermission() {
        getRpcProxy(NotifyClientRpc.class).askPermission();
    }

    protected String getIconResourceKey(int notificationNumber) {
        return "icon-" + notificationNumber;
    }

    protected String getSoundResourceKey(int notificationNumber) {
        return "sound-" + notificationNumber;
    }

    protected String getIconResourceKey(int notificationNumber, NotifyItem notification) {
        return notification.getIcon().map(resource -> {
            String resourceKey = getIconResourceKey(notificationNumber);
            setResource(resourceKey, resource);
            return resourceKey;
        }).orElse(null);
    }

    protected String getSoundResourceKey(int notificationNumber, NotifyItem notification) {
        return notification.getSound().map(resource -> {
            String resourceKey = getSoundResourceKey(notificationNumber);
            setResource(resourceKey, resource);
            return resourceKey;
        }).orElse(null);
    }

    protected SharedNotification generateSharedNotification(NotifyItem item) {
        int notificationNumber = notificationCounter.incrementAndGet();

        SharedNotification shared = new SharedNotification(notificationNumber);
        shared.title = item.getTitle();
        shared.body = item.getBody().orElse(null);
        shared.iconRes = getIconResourceKey(notificationNumber, item);
        shared.hasClickListener = item.getClickListener().isPresent();
        shared.timeoutMs = item.getTimeoutMs().orElse(null);

        return shared;
    }

    protected void instanceShow(NotifyItem notification) {
        SharedNotification shared = generateSharedNotification(notification);
        pendingNotifications.put(shared.id, notification);
        getRpcProxy(NotifyClientRpc.class).showNotification(shared);
    }

    /**
     * Ask permission on client side (if required and supported). Can be used to ask permission already before first
     * notification.
     * @throws NotifyUINotResolvedException Thrown if UI can not be resolved
     */
    public static void askPermission() throws NotifyUINotResolvedException {
        askPermission(resolveUI());
    }

    /**
     * Ask permission on client side (if required and supported). Can be used to ask permission already before first
     * notification.
     * @param ui UI
     */
    public static void askPermission(UI ui) {
        getInstance(ui).instanceAskPermission();
    }

    /**
     * Show notification with title and body
     * @param title Title of notification
     * @param body Body of notification
     * @throws NotifyRuntimeException Exceptions can be thrown in UI can not be resolved, or if notification have been denied
     * by user.
     */
    public static void show(String title, String body) throws NotifyRuntimeException {
        show(title, body, null);
    }

    public static void show(String title, String body, Resource icon) throws NotifyRuntimeException {
        show(resolveUI(), title, body, icon);
    }

    /**
     * Show notification
     * @param notification Notification shown
     * @throws NotifyRuntimeException In case of error
     */
    public static void show(NotifyItem notification) throws NotifyRuntimeException {
        show(resolveUI(), notification);
    }

    /**
     * Show notification at given UI. This method can be also called from other threads.
     * @param ui UI where notification will be shown
     * @param title Title of notification
     * @param body Body of notification (optional)
     * @throws NotifyRuntimeException In case of error
     */
    public static void show(UI ui, String title, String body) throws NotifyRuntimeException {
        show(ui, new NotifyItem().setTitle(title).setBody(body));
    }

    /**
     * Show notification at given UI. This method can be also called from other threads.
     * @param ui UI where notification will be shown
     * @param title Title of notification
     * @param body Body of notification (optional)
     * @param icon Icon of notification (optional)
     * @throws NotifyRuntimeException In case of error
     */
    public static void show(UI ui, String title, String body, Resource icon) throws NotifyRuntimeException {
        show(ui, new NotifyItem().setTitle(title).setBody(body).setIcon(icon));
    }

    /**
     * Show notification
     * @param ui UI
     * @param notification Notification shown
     * @throws NotifyRuntimeException In case of error
     */
    public static void show(UI ui, NotifyItem notification) throws NotifyRuntimeException {
        if(ui == null) {
            throw new NotifyUINotResolvedException();
        }

        if(UI.getCurrent() != ui) {
            ui.access(() -> internalShow(ui, notification));
        } else {
            internalShow(ui, notification);
        }
    }

    private static void internalShow(UI ui, NotifyItem item) throws NotifyRuntimeException {
        Notify instance = getInstance(ui);
        if (instance.clientState == NotifyState.PERMISSION_DENIED) {
            throw new NotificationsDeniedByUserException();
        } else if(instance.clientState == NotifyState.NOT_SUPPORTED) {
            throw new NotificationAPINotSupportedException();
        }
        instance.instanceShow(item);
    }

    protected void instanceAddListener(NotifyStateListener listener) {
        stateListeners.add(listener);
    }

    protected void instanceRemoveListener(NotifyStateListener listener) {
        stateListeners.remove(listener);
    }

    /**
     * Add Notify state listener
     * @param listener Listener added
     * @throws NotifyUINotResolvedException If method fails to resolve UI
     */
    public static void addStateListener(NotifyStateListener listener) throws NotifyUINotResolvedException {
        addStateListener(resolveUI(), listener);
    }

    /**
     * Add Notify state listener
     * @param ui UI
     * @param listener Listener added
     */
    public static void addStateListener(UI ui, NotifyStateListener listener) {
        getInstance(ui).instanceAddListener(listener);
    }

    /**
     * Remove Notify state listener
     * @param listener Listener removed
     * @throws NotifyUINotResolvedException If method fails to resolve UI
     */
    public static void removeStateListener(NotifyStateListener listener) throws NotifyUINotResolvedException {
        removeStateListener(resolveUI(), listener);
    }

    /**
     * Remove Notify state listener
     * @param ui UI
     * @param listener Listener removed
     */
    public static void removeStateListener(UI ui, NotifyStateListener listener) {
        getInstance(ui).instanceRemoveListener(listener);
    }

    protected NotifyState instanceGetState() {
        return clientState;
    }

    /**
     * Get current client state
     * @param ui UI
     * @return What is client state of Notification API
     */
    public static NotifyState getClientState(UI ui) {
        return getInstanceOptional(ui).map(e -> e.clientState).orElse(NotifyState.UNINITIALIZED);
    }

    /**
     * Get current client state
     * @return What is client state of Notification API
     * @throws NotifyUINotResolvedException If method fails to resolve UI
     */
    public static NotifyState getClientState() throws NotifyUINotResolvedException {
        return getClientState(resolveUI());
    }

    private static UI resolveUI() throws NotifyUINotResolvedException {
        return Optional.ofNullable(UI.getCurrent()).orElseThrow(() -> new NotifyUINotResolvedException());
    }

    private void instanceSetDefaultTimeout(Integer milliseconds) {
        if(milliseconds != null && milliseconds < 0) {
            throw new IllegalArgumentException("Invalid timeout value of " + milliseconds);
        }
        getState().defaultTimeoutMs = milliseconds;
    }

    /**
     * Define default timeout for notifications in milliseconds
     * @param milliseconds How many milliseconds notifications will stay open. With 0 user needs to close notifications.
     * @throws NotifyUINotResolvedException If method fails to resolve UI
     */
    public static void setDefaultTimeout(Integer milliseconds) throws NotifyUINotResolvedException {
        setDefaultTimeout(resolveUI(), milliseconds);
    }

    /**
     * Define default timeout for notifications in milliseconds
     * @param ui UI
     * @param milliseconds How many milliseconds notifications will stay open. With 0 user needs to close notifications.
     */
    public static void setDefaultTimeout(UI ui, Integer milliseconds) {
        getInstance(ui).instanceSetDefaultTimeout(milliseconds);
    }

    /**
     * Define if notes should be closed when clicked
     * @param closeOnClick true to close notes when clicked
     * @throws NotifyUINotResolvedException If method fails to resolve UI
     */
    public static void setCloseOnClick(boolean closeOnClick) throws NotifyUINotResolvedException {
        setCloseOnClick(resolveUI(), closeOnClick);
    }

    /**
     * Define if notes should be closed when clicked
     * @param ui UI
     * @param closeOnClick true to close notes when clicked
     */
    public static void setCloseOnClick(UI ui, boolean closeOnClick) {
        getInstance(resolveUI()).getState().closeOnClick = closeOnClick;
    }

}
