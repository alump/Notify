package org.vaadin.alump.notify.client.share;

import com.vaadin.shared.communication.ClientRpc;

import java.util.List;

/**
 * ClientRpc of Vibrate
 */
public interface VibrateClientRpc extends ClientRpc {

    void vibrate(int millisecs);

    void vibratePattern(List<VibrateStep> steps);

    public static class VibrateStep {
        public boolean vibrate = true;
        public int millisecs = 100;
    }
}
