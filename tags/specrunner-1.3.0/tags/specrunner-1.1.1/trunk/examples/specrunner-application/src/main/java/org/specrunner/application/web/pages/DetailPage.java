package org.specrunner.application.web.pages;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.specrunner.application.entities.Employee;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.BasePage;
import org.specrunner.application.web.pages.panels.detail.CommomPanel;
import org.specrunner.application.web.pages.panels.detail.DetailEmployee;
import org.specrunner.application.web.pages.panels.detail.DetailPerson;

public class DetailPage extends BasePage {

    @SuppressWarnings("serial")
    public DetailPage(Person p) {
        Form<Person> form = new Form<Person>("detailPerson");
        content.add(form);

        form.add(new CommomPanel("commom", p));

        if (p instanceof Employee) {
            form.add(new DetailEmployee("detail", p));
        } else {
            form.add(new DetailPerson("detail", p));
        }

        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                continueToOriginalDestination();
            }
        });

        addOthers(form, p);
    }

    protected void addOthers(Form<Person> form, Person p) {
    }
}