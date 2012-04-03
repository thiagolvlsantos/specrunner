package org.specrunner.application.web.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.application.dao.PersonQuery;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.BasePage;
import org.specrunner.application.web.pages.panels.list.PanelList;

@SuppressWarnings("serial")
public class SearchPage extends BasePage {

    public SearchPage(final PageParameters parameters) {
        final PersonQuery query = new PersonQuery();

        Form<Person> form = new Form<Person>("searchPerson");
        content.add(form);
        form.add(new TextField<Person>("txtFirstName", new PropertyModel<Person>(query, "firstName")));
        form.add(new TextField<Person>("txtLastName", new PropertyModel<Person>(query, "lastName")));
        form.add(new Button("search"));
        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                redirectToInterceptPage(new CreatePage());
            }
        });

        content.add(new PanelList("panel", query));
    }
}