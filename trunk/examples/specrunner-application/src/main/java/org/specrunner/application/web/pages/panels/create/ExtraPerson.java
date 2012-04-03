package org.specrunner.application.web.pages.panels.create;

import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.panels.PersonPanel;

@SuppressWarnings("serial")
public class ExtraPerson extends PersonPanel {

    public ExtraPerson(String id) {
        super(id);
        setOutputMarkupId(true);
        setMarkupId("extraPerson");
    }

    @Override
    protected void updateModels(Person person) {
        // none
    }
}
