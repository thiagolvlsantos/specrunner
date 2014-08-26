package org.specrunner.all.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.all.UserSession;
import org.specrunner.all.entity.User;
import org.specrunner.all.entity.UserDao;

@SuppressWarnings("serial")
public class Index extends WebPage {

    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FeedbackPanel("feedback"));
        Form<User> f = new Form<User>("form");
        f.add(new TextField<String>("user", new PropertyModel<String>(this, "user.usr")).setRequired(true));
        f.add(new PasswordTextField("password", new PropertyModel<String>(this, "user.pwd")).setRequired(true));
        f.add(new Button("send") {
            @Override
            public void onSubmit() {
                try {
                    User current = UserDao.lookup(user);
                    ((UserSession) getSession()).setUser(current);
                    user = new User();
                    setResponsePage(AllInOne.class);
                } catch (Throwable e) {
                    error(e.getMessage());
                }
            }
        });
        add(f);
    }
}