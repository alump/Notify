package org.vaadin.alump.notify.client;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.notify.client.share.VibrateClientRpc;
import org.vaadin.alump.notify.client.share.VibrateServerRpc;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Connect(org.vaadin.alump.notify.Vibrate.class)
public class VibrateConnector extends AbstractExtensionConnector {

    private static final boolean IS_SUPPORTED = isSupported();

    private final VibrateClientRpc clientRpc = new VibrateClientRpc() {

        @Override
        public void vibrate(int millisecs) {
            if(IS_SUPPORTED) {
                VibrateConnector.vibrate(millisecs);
            }
        }

        @Override
        public void vibratePattern(List<VibrateStep> steps) {
            if(IS_SUPPORTED) {
                vibrateStep(new LinkedList<VibrateStep>(steps));
            }
        }
    };

    protected void init() {
        super.init();

        registerRpc(VibrateClientRpc.class, clientRpc);
    }

    @Override
    protected void extend(ServerConnector serverConnector) {
        getRpcProxy(VibrateServerRpc.class).supportResolved(IS_SUPPORTED);
    }

    /**
     * Check if Vibrate API is supported by client
     * @return true if supported
     */
    private static native boolean isSupported()
    /*-{
        if(navigator.vibrate) {
            return true;
        } else {
            return false;
        }
    }-*/;

    /**
     * Call native
     * @param millisecs
     */
    private static native void vibrate(int millisecs)
    /*-{
        if(millisecs < 0) {
            navigator.vibrate();
        } else {
            navigator.vibrate(millisecs);
        }
    }-*/;

    private void vibrateStep(final Queue<VibrateClientRpc.VibrateStep> steps) {
        VibrateClientRpc.VibrateStep next = steps.remove();
        if(next.vibrate) {
            vibrate(next.millisecs);
        }
        if(!steps.isEmpty()) {
            Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                @Override
                public boolean execute() {
                    vibrateStep(steps);
                    return false;
                }
            }, next.millisecs);
        }
    }

}
