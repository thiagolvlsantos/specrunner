package org.specrunner.application.web.pages;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.specrunner.application.entities.Person;

@SuppressWarnings("serial")
public class DeletePage extends DetailPage {

    public DeletePage(Person p) {
        super(p);
    }

    @Override
    protected void addOthers(Form<Person> form, final Person p) {
        form.add(new Button("delete") {
            @Override
            public void onSubmit() {
                personDao.delete(p);
                continueToOriginalDestination();
            }
        });
    }
}
