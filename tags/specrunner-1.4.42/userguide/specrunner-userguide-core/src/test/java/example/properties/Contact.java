package example.properties;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Contact {

    private String name;

    private DateTime expire;

    private LocalDate data;

    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getExpire() {
        return expire;
    }

    public void setExpire(DateTime expire) {
        this.expire = expire;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}