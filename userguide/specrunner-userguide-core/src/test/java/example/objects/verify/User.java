package example.objects.verify;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;

public class User {

    private String name;
    private LocalDate birthday;
    private Collection<String> emails;
    private Address workplace;
    private Address[] addresses;
    private List<User> parents;

    public User(String name, LocalDate birthday, Collection<String> emails, Address workplace, Address[] addresses, List<User> parents) {
        super();
        this.name = name;
        this.birthday = birthday;
        this.emails = emails;
        this.workplace = workplace;
        this.addresses = addresses;
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Collection<String> getEmails() {
        return emails;
    }

    public void setEmails(Collection<String> emails) {
        this.emails = emails;
    }

    public Address getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Address workplace) {
        this.workplace = workplace;
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