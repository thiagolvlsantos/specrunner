package org.specrunner.application.web;

import java.util.Arrays;

import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.specrunner.application.dao.PersonDao;
import org.specrunner.application.dao.UnitDao;
import org.specrunner.application.entities.Employee;
import org.specrunner.application.entities.Naming;
import org.specrunner.application.entities.Person;
import org.specrunner.application.entities.Unit;

public class BasePage extends WebPage {

    private static ThreadLocal<Boolean> loaded = new ThreadLocal<Boolean>();
    protected WebMarkupContainer content;

    @SpringBean
    protected PersonDao personDao;
    @SpringBean
    protected UnitDao unitDao;

    public BasePage() {
        content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        content.setMarkupId("idContent");
        add(content);

        content.add(new FeedbackPanel("feedback"));

        add(CSSPackageResource.getHeaderContribution(BasePage.class, "style.css"));

        if (loaded.get() == null) {
            for (int i = 0; i < 17; i++) {
                Person p = (i % 2 == 0 ? new Person() : new Employee());
                Naming n = new Naming();
                n.setFirst("First " + i);
                n.setLast("Last " + i);
                p.setNaming(n);
                if (i % 2 != 0) {
                    ((Employee) p).setSalary(i * 10.0);
                    Unit u = new Unit();
                    u.setName("no_" + i);
                    for (int k = 0; k < i; k++) {
                        Unit c1 = new Unit();
                        c1.setName("c_" + k + "_" + i);
                        u.add(c1);
                    }
                    ((Employee) p).setUnits(Arrays.asList(u));
                }
                personDao.update(p);
            }
            loaded.set(true);
        }
    }
}