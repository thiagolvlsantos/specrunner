package org.specrunner.application.web.pages.panels.create;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.panels.PersonPanel;

@SuppressWarnings("serial")
public class ExtraEmployee extends PersonPanel {

    private TextField<Double> txtSalary;

    public ExtraEmployee(String id, Person person) {
        super(id);
        setOutputMarkupId(true);
        setMarkupId("extraEmployee");
        add(txtSalary = new TextField<Double>("txtSalary", bindSalary(person)));
    }

    protected PropertyModel<Double> bindSalary(Person person) {
        return new PropertyModel<Double>(person, "salary");
    }

    @Override
    protected void updateModels(Person person) {
        System.out.println("UPDATE_MODEL:" + person);
        txtSalary.setModel(bindSalary(person));
    }
}
