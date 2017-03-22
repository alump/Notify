package org.vaadin.alump.notify;

import com.vaadin.ui.UI;
import org.vaadin.alump.notify.client.share.NotifyState;

import java.io.Serializable;

/**
 * Event given to NotifyStateListeners
 */
public class NotifyStateEvent implements Serializable {
    private final UI ui;
    private final NotifyState oldState;
    private final NotifyState newState;

    public NotifyStateEvent(UI ui, NotifyState oldState, NotifyState newState) {
        this.ui = ui;
        this.oldState = oldState;
        this.newState = newState;
    }

    public UI getUI () {
        return ui;
    }

    public NotifyState getOldState() {
        return oldState;
    }

    public NotifyState getNewState() {
        return newState;
    }

    public boolean isReady() {
        return newState == NotifyState.READY;
    }
}
