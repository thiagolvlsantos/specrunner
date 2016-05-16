package example.objects.collections;

import java.util.List;

public class Contact {

    private String email;
    private List<Phone> phones;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "Contact [email=" + email + ", phones=" + phones + "]";
    }
}
