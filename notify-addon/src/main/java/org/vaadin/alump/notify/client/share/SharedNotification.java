package org.vaadin.alump.notify.client.share;

/**
 * Shared notification used to when sending data from server to client
 */
public class SharedNotification {
    public int id;
    public String title = null;
    public String body = null;
    public String iconRes = null;
    public String soundRes = null;
    public boolean hasClickListener = false;
    public Integer timeoutMs = null;

    public SharedNotification() {

    }

    public SharedNotification(int id) {
        this.id = id;
    }
}
