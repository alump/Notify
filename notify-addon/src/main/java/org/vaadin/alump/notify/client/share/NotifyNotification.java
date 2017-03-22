package org.vaadin.alump.notify.client.share;

/**
 * Created by alump on 21/03/2017.
 */
public class NotifyNotification {
    public String title;
    public String body;
    public String iconRes;

    public NotifyNotification() {

    }

    public NotifyNotification(String title, String body, String iconRes) {
        this.title = title;
        this.body = body;
        this.iconRes = iconRes;
    }
}
