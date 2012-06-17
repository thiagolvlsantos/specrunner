package org.specrunner.application.web.pages.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.specrunner.application.entities.Person;

@SuppressWarnings("serial")
public abstract class PersonPanel extends Panel {

    private Person person;

    public PersonPanel(String id) {
        super(id);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        updateModels(person);
    }

    protected abstract void updateModels(Person person);
}
