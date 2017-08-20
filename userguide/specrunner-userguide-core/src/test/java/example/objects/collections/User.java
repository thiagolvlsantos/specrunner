package example.objects.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class User {
    private String name;
    private List<Id> ids;
    private Collection<String> aliases;
    private Contact[] contacts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Id> getIds() {
        return ids;
    }

    public void setIds(List<Id> ids) {
        this.ids = ids;
    }

    public Collection<String> getAliases() {
        return aliases;
    }

    public void setAliases(Collection<String> aliases) {
        this.aliases = aliases;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public void setContacts(Contact[] contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", ids=" + ids + ", aliases=" + aliases + ", contacts=" + Arrays.toString(contacts) + "]";
    }
}
