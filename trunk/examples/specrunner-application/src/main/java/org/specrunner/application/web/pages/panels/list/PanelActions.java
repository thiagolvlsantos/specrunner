package org.specrunner.application.web.pages.panels.list;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.specrunner.application.entities.Person;
import org.specrunner.application.web.pages.DeletePage;
import org.specrunner.application.web.pages.DetailPage;
import org.specrunner.application.web.pages.UpdatePage;
import org.specrunner.application.web.pages.panels.PersonPanel;
import org.specrunner.application.web.pages.res.Images;

@SuppressWarnings("serial")
public class PanelActions extends PersonPanel {

    public PanelActions(String id, final Person p) {
        super(id);
        setPerson(p);

        Form<Person> form = new Form<Person>("actions");
        add(form);

        ImageButton b;
        b = new ImageButton("detail") {
            @Override
            public void onSubmit() {
                redirectToInterceptPage(new DetailPage(p));
            }
        };
        b.setImageResourceReference(Images.IMG_DETAIL);
        form.add(b);

        b = new ImageButton("update") {
            @Override
            public void onSubmit() {
                redirectToInterceptPage(new UpdatePage(p));
            }
        };
        b.setImageResourceReference(Images.IMG_UPDATE);
        form.add(b);

        b = new ImageButton("delete") {
            @Override
            public void onSubmit() {
                redirectToInterceptPage(new DeletePage(p));
            }
        };
        b.setImageResourceReference(Images.IMG_DELETE);
        form.add(b);
    }

    @Override
    protected void updateModels(Person person) {
    }
}
