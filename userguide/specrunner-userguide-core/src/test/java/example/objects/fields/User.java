package example.objects.fields;

import org.joda.time.LocalDate;

public class User {

    private String name;
    private Contact contact;
    private String anything;
    private LocalDate birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getAnything() {
        return anything;
    }

    public void setAnything(String anything) {
        this.anything = anything;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", contact=" + contact + ", anything=" + anything + ", birthday=" + birthday + "]";
    }
}
