package org.specrunner.application.web.pages.panels.detail;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.panels.PersonPanel;

@SuppressWarnings("serial")
public class CommomPanel extends PersonPanel {

    public CommomPanel(String id, Person p) {
        super(id);
        add(new Label("first", new PropertyModel<String>(p, "naming.first")));
        add(new Label("last", new PropertyModel<String>(p, "naming.last")));
    }

    @Override
    protected void updateModels(Person person) {
        // nothing
    }
}
