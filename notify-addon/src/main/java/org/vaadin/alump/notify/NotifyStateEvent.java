/**
 * NotifyStateEvent.java (Notify)
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
