package example.objects.fields;

public class User {

    private String name;
    private Contact contact;
    private String anything;

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

    @Override
    public String toString() {
        return "User [name=" + name + ", contact=" + contact + ", anything=" + anything + "]";
    }
}