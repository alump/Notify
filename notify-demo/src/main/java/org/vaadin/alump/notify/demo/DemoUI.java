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
package org.vaadin.alump.notify.demo;

import com.vaadin.annotations.Push;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.notify.*;
import org.vaadin.alump.notify.client.share.NotifyState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Theme("demo")
@Title("Notify Add-on Demo")
@Push
@SuppressWarnings("serial")
public class DemoUI extends UI implements NotifyStateListener
{
    private TextField title;
    private TextField body;
    private ComboBox<Resource> icon;
    private Label stateLabel;
    private AtomicInteger delayedCounter = new AtomicInteger(0);
    private Collection<Component> disableIfDenied = new ArrayList<>();

    private static final int DELAY_SECONDS = 3;
    private static final int TIMEOUT_SECONDS = 2;
    private static final String GITHUB_URL = "https://github.com/alump/Notify";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.alump.notify.demo.WidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);


        Label header = new Label("Notify Vaadin Add-on");
        header.addStyleName(ValoTheme.LABEL_H1);
        layout.addComponent(header);

        Label info = new Label("Notify allows you access HTML5 Notification API with server side Java API. "
                + "Notice that IE11 and Edge browsers do not support Notication API. Also remember that browser might "
                + "limit amount of notification shows at the same time (eg. in Chrome it's 3). Also Chrome's incognito "
                + "mode will always denied permissions automatically. Firefox only shows one notification, so still "
                + "shown notification (from same page) will get replaced. Timeout behaviors are quite different on "
                + "all browsers");
        info.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(info);

        layout.addComponent(createInfoAndSettings());

        layout.addComponent(createNotifyButtons());

        layout.addComponent(createNotifyEditor());

        layout.addComponent(createVibrate());

        Link gitHubLink = new Link("Project's GitHub page",
                new ExternalResource(GITHUB_URL));
        layout.addComponent(gitHubLink);
    }

    private Component createInfoAndSettings() {
        HorizontalLayout infoRow = new HorizontalLayout();
        infoRow.setCaption("Info and Settings");
        infoRow.setSpacing(true);
        infoRow.setMargin(new MarginInfo(false, false, false, true));

        stateLabel = new Label(Notify.getClientState().name());
        stateLabel.setCaption("Notify Client State");
        infoRow.addComponent(stateLabel);

        ComboBox<Integer> defaultTimeout = new ComboBox<>();
        defaultTimeout.setCaption("Default timeout");
        defaultTimeout.setItems(0, 5000, 10000, 30000, 60000);
        defaultTimeout.setItemCaptionGenerator(value -> {
            if(value == 0) {
                return "No timeout";
            } else {
                return "" + (value / 1000) + " seconds";
            }
        });
        defaultTimeout.addValueChangeListener(e -> Notify.setDefaultTimeout(e.getValue()));
        defaultTimeout.setValue(10000);
        infoRow.addComponent(defaultTimeout);
        disableIfDenied.add(defaultTimeout);

        CheckBox closeOnClick = new CheckBox("Close notifications when clicked");
        closeOnClick.addValueChangeListener(e -> Notify.setCloseOnClick(e.getValue()));
        infoRow.addComponent(closeOnClick);
        disableIfDenied.add(closeOnClick);

        return infoRow;
    }

    private Component createNotifyButtons() {
        HorizontalLayout buttonRow = new HorizontalLayout();
        buttonRow.setCaption("Actions");
        buttonRow.setSpacing(true);
        buttonRow.setMargin(new MarginInfo(false, false, false, true));

        Button askPermission = new Button("Ask permission", e -> Notify.askPermission());
        buttonRow.addComponent(askPermission);

        Button showItButton = new Button("Show example", e -> showIt());
        disableIfDenied.add(showItButton);
        buttonRow.addComponent(showItButton);

        Button showItDelayedButton = new Button("Show example with " + DELAY_SECONDS + "s delay",
                e -> showItDelayed());
        disableIfDenied.add(showItDelayedButton);
        buttonRow.addComponent(showItDelayedButton);

        Button showClickable = new Button("Show clickable", e -> showWithClickHandling());
        disableIfDenied.add(showClickable);
        buttonRow.addComponent(showClickable);

        Button showTimeout = new Button("Show with " + TIMEOUT_SECONDS + "s timeout", e -> showWithTimeout());
        disableIfDenied.add(showTimeout);
        buttonRow.addComponent(showTimeout);

        /*
        Button showSound = new Button("With sound", e -> showWithSound());
        disableIfDenied.add(showSound);
        buttonRow.addComponent(showSound);
        */

        return buttonRow;
    }

    private Component createNotifyEditor() {
        HorizontalLayout editor = new HorizontalLayout();
        editor.setCaption("Notification editor");
        editor.setSpacing(true);
        editor.setMargin(new MarginInfo(false, false, false, true));

        title = new TextField();
        title.setCaption("Title");
        title.setValue("Write title here");
        editor.addComponent(title);

        body = new TextField();
        body.setCaption("Body");
        body.setValue("Write body here");
        editor.addComponent(body);

        icon = new ComboBox<>();
        icon.setCaption("Icon");
        ThemeResource defaultValue = new ThemeResource("images/yunowork.png");
        icon.setItems(
                new ThemeResource("images/chavatar.png"),
                new ThemeResource("images/cat.jpg"),
                new ThemeResource("images/sound.png"),
                new ThemeResource("images/github.png"),
                defaultValue);
        icon.setItemCaptionGenerator(resource -> {
            if(resource instanceof ThemeResource) {
                ThemeResource themeResource = (ThemeResource)resource;
                String[] parts = themeResource.getResourceId().split("/");
                return parts[parts.length - 1];
            } else {
                return resource.toString();
            }
        });
        icon.setValue(defaultValue);
        editor.addComponent(icon);

        Button showButton = new Button("← Show that!", this::showEditor);
        editor.addComponent(showButton);
        disableIfDenied.add(showButton);
        editor.setComponentAlignment(showButton, Alignment.BOTTOM_CENTER);

        Button delayButton = new Button("←← Show with " + DELAY_SECONDS + "s delay", this::showEditorWithDelay);
        editor.addComponent(delayButton);
        disableIfDenied.add(delayButton);
        editor.setComponentAlignment(delayButton, Alignment.BOTTOM_CENTER);

        return editor;
    }

    private Component createVibrate() {
        HorizontalLayout vibrateRow = new HorizontalLayout();
        vibrateRow.setCaption("Vibrate");
        vibrateRow.setSpacing(true);
        vibrateRow.setMargin(new MarginInfo(false, false, false, true));

        Button vibrateButton = new Button("Vibrate", e -> {
            if(Vibrate.isSupported().orElse(true)) {
                Vibrate.vibrate(200);
            } else {
                Notification.show("Looks like Vibrate API is not supported");
            }
        });
        vibrateRow.addComponent(vibrateButton);

        return vibrateRow;
    }

    @Override
    public void attach() {
        super.attach();
        Notify.addStateListener(getUI(),this);
    }

    @Override
    public void detach() {
        Notify.removeStateListener(getUI(),this);
        super.detach();
    }

    private void showEditor(Button.ClickEvent event) {
        Notify.show(title.getValue(), body.getValue(), icon.getValue());
    }

    private void showEditorWithDelay(Button.ClickEvent event) {
        final String titleValue = title.getValue();
        final String bodyValue = body.getValue();
        final Resource iconValue = icon.getValue();

        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(DELAY_SECONDS * 1000);
            } catch (InterruptedException e) {
                return;
            }

            if(DemoUI.this.isAttached()) {
                Notify.show(DemoUI.this, titleValue, bodyValue, iconValue);
            }
        });
        delayThread.start();
    }

    private void showIt() {
        Notify.show(new NotifyItem()
                .setTitle("Notify Vaadin Add-on")
                .setBody("Hey it works? Doesn't it?")
                .setIcon(new ThemeResource("images/chavatar.png")));
    }

    private void showWithTimeout() {
        Notify.show(new NotifyItem()
                .setTitle("I'm busy")
                .setBody("I will close myself in " + TIMEOUT_SECONDS + " seconds.")
                .setIcon(new ThemeResource("images/cat.jpg"))
                .setTimeout(TIMEOUT_SECONDS));
    }

    /*
    private void showWithSound() {
        Notify.show(new NotifyItem()
                .setTitle("With sound")
                .setBody("With SOME browsers you should hear something... I hope.")
                .setIcon(new ThemeResource("images/sound.png"))
                .setSound(new ThemeResource("sounds/notification.mp3"))
                .setTimeout(3));
    }
    */

    private void showWithClickHandling() {
        Notify.show(new NotifyItem()
                .setTitle("Notification can be also clicked")
                .setBody("Click here to navigate to project's GitHub page")
                .setIcon(new ThemeResource("images/github.png"))
                .setClickListener(e -> {
                    Page.getCurrent().setLocation(GITHUB_URL);
                }));
    }

    private void showItDelayed() {
        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(DELAY_SECONDS * 1000);
            } catch (InterruptedException e) {
                return;
            }

            if(DemoUI.this.isAttached()) {
                Notify.show(DemoUI.this, "Delayed example #" + delayedCounter.incrementAndGet(),
                        "I was delayed for " + DELAY_SECONDS + " seconds",
                        new ThemeResource("images/cat.jpg"));
            }
        });
        delayThread.start();
    }

    @Override
    public void onNotifyStateChange(NotifyStateEvent event) {
        if(stateLabel != null) {
            stateLabel.setValue(event.getNewState().name());
            disableIfDenied.forEach(d -> d.setEnabled(
                    event.getNewState() != NotifyState.NOT_SUPPORTED
                            && event.getNewState() != NotifyState.PERMISSION_DENIED));
        }
    }
}
