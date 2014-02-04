package org.specrunner.application.web.pages.panels.detail;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.panels.PersonPanel;

@SuppressWarnings("serial")
public class DetailEmployee extends PersonPanel {

    public DetailEmployee(String id, Person p) {
        super(id);
        add(new Label("salary", bindSalary(p)));
    }

    protected PropertyModel<String> bindSalary(Person p) {
        return new PropertyModel<String>(p, "salary");
    }

    @Override
    protected void updateModels(Person person) {
    }
}
