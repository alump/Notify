/**
 * NotifyItem.java (Notify)
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

import com.vaadin.server.Resource;

/**
 * Notify notification. Called NotifyItem to avoid conflicts with Notification class on Vaadin.
 */
public class NotifyItem {
    private String title;
    private String body;
    private Resource icon;
    private Resource sound;
    private NotifyClickListener clickListener;
    private Integer timeoutMs;

    /**
     * Create new notification
     */
    public NotifyItem() {

    }

    /**
     * Set title (first row) of notification
     * @param title Title of notification
     * @return This instance, allows calling set methods after new
     */
    public NotifyItem setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set body (second row) of notification
     * @param body Body of notification
     * @return This instance, allows calling set methods after new
     */
    public NotifyItem setBody(String body) {
        if(body == null || body.isEmpty()) {
            this.body = null;
        } else {
            this.body = body;
        }
        return this;
    }

    /**
     * Set icon of notification
     * @param icon Icon resource of notification
     * @return This instance, allows calling set methods after new
     */
    public NotifyItem setIcon(Resource icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Set sound of notification (hidden as experimental, and not well supported by browsers)
     * @param sound Sound resource of notification
     * @return This instance, allows calling set methods after new
     */
    protected NotifyItem setSound(Resource sound) {
        this.sound = sound;
        return this;
    }

    /**
     * Set click listener for notification
     * @param listener Listener called when notification is clicked by user
     * @return This instance, allows calling set methods after new
     */
    public NotifyItem setClickListener(NotifyClickListener listener) {
        this.clickListener = listener;
        return this;
    }

    /**
     * Define timeout of notification in milliseconds. Timeouts work differently on different browsers.
     * @param milliseconds How many milliseconds the notification is shown. With 0 notification will stay open until
     *                     closed by user. Null would tell to use default timeout.
     * @return This instance, allows calling set methods after new
     */
    public NotifyItem setTimeoutMs(Integer milliseconds) {
        if(milliseconds != null && milliseconds < 1) {
            throw new IllegalArgumentException("Invalid timeout value " + milliseconds);
        }
        timeoutMs = milliseconds;
        return this;
    }

    /**
     * Define timeout of notification in seconds. Timeouts work differently on different browsers.
     * @param seconds How many seconds the notification is shown. With 0 notification will stay open until
     *                     closed by user. Null would tell to use default timeout.
     * @return This instance, allows calling sets after new
     */
    public NotifyItem setTimeout(Integer seconds) {
        return setTimeoutMs(seconds * 1000);
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Resource getIcon() {
        return icon;
    }

    public Resource getSound() {
        return sound;
    }

    public NotifyClickListener getClickListener() {
        return clickListener;
    }

    /**
     * Get timeout of this notification
     * @return Specific timeout for this notification if defined, in milliseconds
     */
    public Integer getTimeoutMs() {
        return timeoutMs;
    }

}
