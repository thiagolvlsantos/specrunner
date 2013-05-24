package org.specrunner.application.web.pages.panels.list;

import java.util.Iterator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.specrunner.application.dao.PersonDao;
import org.specrunner.application.dao.PersonQuery;
import org.specrunner.application.entities.Employee;
import org.specrunner.application.entities.Person;
import org.specrunner.application.util.tree.TreeModelIComposite;

@SuppressWarnings("serial")
public class PanelList extends Panel {

    @SpringBean
    private PersonDao personDao;

    public PanelList(String id, final PersonQuery query) {
        super(id);

        final ISortableDataProvider<Person> provider = new SortableDataProvider<Person>() {
            public Iterator<? extends Person> iterator(int first, int count) {
                query.setFirst(first);
                query.setCount(count);
                if (getSort() != null) {
                    query.setAscending(getSort().isAscending());
                    query.setProperty(getSort().getProperty());
                }
                return personDao.find(query);
            }

            public int size() {
                return personDao.total(query);
            }

            public IModel<Person> model(Person object) {
                return Model.of(object);
            }
        };

        final DataView<Person> table = new DataView<Person>("persons", provider, 5) {
            @Override
            protected void populateItem(Item<Person> item) {
                Person p = item.getModelObject();
                item.add(new Label("type", p.getType().getName()));
                item.add(new Label("first", p.getNaming().getFirst()));
                item.add(new Label("last", p.getNaming().getLast()));
                item.add(new Label("salary", "" + (p instanceof Employee ? ((Employee) p).getSalary() : "")));
                if (p instanceof Employee) {
                    item.add(new LinkTreeRegular("units", "name", "link", new TreeModelIComposite(((Employee) p).getUnits().get(0))));
                } else {
                    item.add(new Label("units", ""));
                }
                item.add(new PanelActions("actions", p));
            }

            @Override
            public boolean isVisible() {
                return getItemCount() > 0;
            }
        };

        final WebMarkupContainer data = new WebMarkupContainer("data") {
            @Override
            public boolean isVisible() {
                return table.isVisible();
            }
        };
        data.setOutputMarkupId(true);
        data.setMarkupId("idData");
        add(data);

        data.add(table);

        AjaxFallbackOrderByLink orderFirst = new AjaxFallbackOrderByLink("orderFirst", "naming.first", provider) {
            @Override
            protected void onAjaxClick(AjaxRequestTarget target) {
                target.addComponent(data);
            }
        };
        data.add(orderFirst);

        AjaxFallbackOrderByLink orderSalary = new AjaxFallbackOrderByLink("orderSalary", "salary", provider) {
            @Override
            protected void onAjaxClick(AjaxRequestTarget target) {
                target.addComponent(data);
            }
        };
        data.add(orderSalary);

        AjaxPagingNavigator nav = new AjaxPagingNavigator("nav", table);
        data.add(nav);

        Label none = new Label("none", "None results found.") {
            @Override
            public boolean isVisible() {
                return !data.isVisible();
            }
        };
        add(none);
    }
}