package org.specrunner.application.web.pages;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.specrunner.application.dao.PersonQuery;
import org.specrunner.application.entities.Employee;
import org.specrunner.application.entities.Person;
import org.specrunner.application.entities.PersonType;
import org.specrunner.application.web.BasePage;
import org.specrunner.application.web.pages.panels.create.ExtraEmployee;
import org.specrunner.application.web.pages.panels.create.ExtraPerson;
import org.specrunner.application.web.pages.panels.list.PanelList;

@SuppressWarnings("serial")
public class CreatePage extends BasePage {

    private Person person = new Person();
    private TextField<Person> txtFirstName;
    private TextField<Person> txtLastName;
    private DropDownChoice<PersonType> dropType;
    private ExtraPerson extraPerson;
    private ExtraEmployee extraEmployee;

    public CreatePage() {
        final Form<Person> form = new Form<Person>("createPerson");
        form.setOutputMarkupId(true);
        form.setMarkupId("idForm");
        content.add(form);

        form.add(txtFirstName = new TextField<Person>("txtFirstName", bindFirst()));
        form.add(txtLastName = new TextField<Person>("txtLastName", bindLast()));

        List<PersonType> types = new LinkedList<PersonType>();
        for (PersonType p : PersonType.values()) {
            types.add(p);
        }
        IModel<PersonType> model = new IModel<PersonType>() {
            private PersonType selected = PersonType.PERSON;

            public void detach() {
            }

            public PersonType getObject() {
                return selected;
            }

            public void setObject(PersonType object) {
                this.selected = object;
            }
        };
        form.add(dropType = new DropDownChoice<PersonType>("dropType", model, types, new IChoiceRenderer<PersonType>() {
            public Object getDisplayValue(PersonType object) {
                return object.getName();
            }

            public String getIdValue(PersonType object, int index) {
                return "" + object.getCode();
            }
        }));

        form.add(extraPerson = new ExtraPerson("extraPerson") {
            @Override
            public boolean isVisible() {
                return dropType.getModelObject() == PersonType.PERSON;
            }
        });

        form.add(extraEmployee = new ExtraEmployee("extraEmployee", person) {
            @Override
            public boolean isVisible() {
                return dropType.getModelObject() == PersonType.EMPLOYEE;
            }
        });

        dropType.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newInstance();
                target.addComponent(form);
            }
        });

        final PanelList list = new PanelList("panel", new PersonQuery());
        list.setOutputMarkupId(true);
        list.setMarkupId("idPanel");

        form.add(new AjaxButton("add") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Set<ConstraintViolation<Person>> violations = personDao.validate(person);
                if (violations.isEmpty()) {
                    personDao.save(person);
                    newInstance();
                } else {
                    for (ConstraintViolation<Person> v : violations) {
                        this.error(v.getPropertyPath() + " " + v.getMessage());
                    }
                }
                target.addComponent(content);
            }
        });

        form.add(new Button("back") {
            @Override
            public void onSubmit() {
                continueToOriginalDestination();
            }
        });

        content.add(list);
    }

    protected void newInstance() {
        Person other;
        if (dropType.getModelObject() == PersonType.EMPLOYEE) {
            other = new Employee();
        } else {
            other = new Person();
        }
        // person.copyTo(other);
        person = other;
        bindModels();
    }

    protected void bindModels() {
        txtFirstName.setModel(bindFirst());
        txtLastName.setModel(bindLast());
        extraPerson.setPerson(person);
        extraEmployee.setPerson(person);
    }

    protected PropertyModel<Person> bindFirst() {
        return new PropertyModel<Person>(person, "naming.first");
    }

    protected PropertyModel<Person> bindLast() {
        return new PropertyModel<Person>(person, "naming.last");
    }
}
