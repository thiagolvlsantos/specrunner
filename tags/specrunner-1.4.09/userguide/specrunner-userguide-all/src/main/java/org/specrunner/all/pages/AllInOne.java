package org.specrunner.all.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.all.entity.User;

@SuppressWarnings("serial")
public class AllInOne extends WebPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("name", new PropertyModel<User>(getSession(), "user.name")));
        add(new Label("message", Model.of("Message for admins!")) {
            @Override
            public boolean isVisible() {
                return new PropertyModel<User>(getSession(), "user").getObject().isAdmin();
            }
        });
    }
}
