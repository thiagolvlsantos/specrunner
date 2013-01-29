package org.specrunner.application.web.pages.panels.detail;

import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.panels.PersonPanel;

@SuppressWarnings("serial")
public class DetailPerson extends PersonPanel {

    public DetailPerson(String id, Person p) {
        super(id);
    }

    @Override
    protected void updateModels(Person person) {
    }
}
