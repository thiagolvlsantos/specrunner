package example.objects.verify;

import java.util.Collection;
import java.util.List;

public class User {

    private String name;
    private Collection<String> emails;
    private Address[] addresses;
    private List<User> parents;

    public User(String name, Collection<String> emails, Address[] addresses, List<User> parents) {
        super();
        this.name = name;
        this.emails = emails;
        this.addresses = addresses;
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getEmails() {
        return emails;
    }

    public void setEmails(Collection<String> emails) {
        this.emails = emails;
    }

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

    public List<User> parents() {
        return parents;
    }
}