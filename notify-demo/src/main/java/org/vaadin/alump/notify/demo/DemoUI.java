package org.vaadin.alump.notify.demo;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.notify.Notify;
import org.vaadin.alump.notify.NotifyStateEvent;
import org.vaadin.alump.notify.NotifyStateListener;

@Theme("demo")
@Title("Notify Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI implements NotifyStateListener
{
    private TextField title;
    private TextField body;
    private ComboBox<Resource> icon;
    private Label stateLabel;

    @Override
    public void onNotifyStateChange(NotifyStateEvent event) {
        if(stateLabel != null) {
            stateLabel.setValue(event.getNewState().name());
        }
    }

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

        stateLabel = new Label(Notify.getClientState().name());
        stateLabel.setCaption("Notify Client State");
        layout.addComponent(stateLabel);

        Button askPermission = new Button("Ask permission", e -> Notify.askPermission());
        layout.addComponent(askPermission);

        Button showItButton = new Button("Show example", this::showIt);
        layout.addComponent(showItButton);

        HorizontalLayout editor = new HorizontalLayout();
        editor.setCaption("Notification editor");
        editor.setSpacing(true);
        layout.addComponent(editor);

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
        icon.setItems(new ThemeResource("images/chavatar.png"));
        editor.addComponent(icon);

        Button showButton = new Button("← Show that!", this::showEditor);
        editor.addComponent(showButton);
        editor.setComponentAlignment(showButton, Alignment.BOTTOM_CENTER);

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

    private void showIt(Button.ClickEvent event) {
        Notify.show("Notify Vaadin Add-on", "Hey it works? Doesn't it?",
                new ThemeResource("images/chavatar.png"));
    }
}
