/**
 * Vibrate.java (Notify)
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

import com.vaadin.server.Extension;
import com.vaadin.ui.UI;
import org.vaadin.alump.notify.client.share.VibrateClientRpc;
import org.vaadin.alump.notify.client.share.VibrateServerRpc;
import org.vaadin.alump.notify.exceptions.VibrateUINotResolvedException;

import java.util.LinkedList;
import java.util.List;

/**
 * Interface to call browser to vibrate the device
 */
public class Vibrate extends AbstractExtension {

    private Boolean clientSupport = null;

    private VibrateServerRpc serverRpc = new VibrateServerRpc() {
        @Override
        public void supportResolved(boolean supported) {
            clientSupport = supported;
        }
    };

    protected Vibrate(UI ui) {
        super();
        registerRpc(serverRpc, VibrateServerRpc.class);
        extend(ui);
    }

    protected static Vibrate get() throws VibrateUINotResolvedException {
        UI ui = UI.getCurrent();
        if(ui == null) {
            throw new VibrateUINotResolvedException();
        }
        return get(UI.getCurrent());
    }

    /**
     * Get instance of Vibrate
     * @param ui UI extended by Vibrate
     * @return Instance of Vibrare
     */
    protected static Vibrate get(UI ui) {
        Vibrate instance = null;
        for (Extension extension : ui.getExtensions()) {
            if(extension instanceof Vibrate) {
                instance = (Vibrate)extension;
                break;
            }
        }

        if(instance == null) {
            instance = new Vibrate(ui);
        }

        return instance;
    }

    /**
     * Vibrate Pattern
     */
    public static class Pattern {
        private final List<VibrateClientRpc.VibrateStep> steps = new LinkedList<VibrateClientRpc.VibrateStep>();

        public Pattern() {

        }

        /**
         * Adds vibrate step to pattern
         * @param millisecs Length of vibrate in milliseconds
         * @return Pattern (for piping commands)
         */
        public Pattern vibrate(int millisecs) {
            return add(millisecs, true);
        }

        /**
         * Adds delay step to pattern
         * @param millisecs Length of delay in milliseconds
         * @return Pattern (for piping command)
         */
        public Pattern delay(int millisecs) {
            return add(millisecs, false);
        }

        /**
         * Adds new step to pattern
         * @param millisecs Length of step
         * @param vibrate true if vibrate, false if delay
         * @return Pattern (for piping command)
         */
        public Pattern add(int millisecs, boolean vibrate) {
            VibrateClientRpc.VibrateStep step = new VibrateClientRpc.VibrateStep();
            step.millisecs = millisecs;
            step.vibrate = vibrate;
            steps.add(step);

            return this;
        }

        /**
         * Get steps in list
         * @return
         */
        protected List<VibrateClientRpc.VibrateStep> getSteps() {
            return steps;
        }
    }

    /**
     * Vibrate given milliseconds
     * @param millisecs Milliseconds vibrated
     */
    public static void vibrate(int millisecs) {
        get().requestVibrate(millisecs);
    }

    /**
     * Vibrate given milliseconds
     * @param ui UI
     * @param millisecs Milliseconds vibrated
     */
    public static void vibrate(UI ui, int millisecs) {
        get(ui).requestVibrate(millisecs);
    }

    /**
     * Creates new pattern to be defined and given to vibrate()
     * @return
     */
    public static Pattern createPattern() {
        return new Pattern();
    }

    /**
     * Vibrate the given pattern
     * @param pattern Pattern vibrated
     */
    public static void vibrate(Pattern pattern) {
        get().requestVibrate(pattern.getSteps());
    }

    /**
     * Vibrate the given pattern
     * @param ui UI
     * @param pattern Pattern vibrated
     */
    public static void vibrate(UI ui, Pattern pattern) {
        get(ui).requestVibrate(pattern.getSteps());
    }

    /**
     * Request client to vibrate
     * @param millisecs
     */
    protected void requestVibrate(int millisecs) {
        if(millisecs < 0) {
            throw new IllegalArgumentException("Negative milliseconds value " + millisecs + " not accepted");
        }
        getRpcProxy(VibrateClientRpc.class).vibrate(millisecs);
    }

    /**
     * Request client to vibrate
     * @param steps
     */
    protected void requestVibrate(List<VibrateClientRpc.VibrateStep> steps) {
        if(steps.isEmpty()) {
            return;
        }
        getRpcProxy(VibrateClientRpc.class).vibratePattern(steps);
    }

    private Boolean instanceIsSupported() {
        return clientSupport;
    }

    public static Boolean isSupported() {
        return get().instanceIsSupported();
    }

    public static Boolean isSupported(UI ui) {
        return get(ui).instanceIsSupported();
    }
}