package example.employee;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Phone {

    private String number;

    @Column(name = "PHO_NUMBER")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Phone [number=" + number + "]";
    }
}
