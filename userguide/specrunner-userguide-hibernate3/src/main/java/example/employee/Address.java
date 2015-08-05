package example.employee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ADD_ADDRESS", schema = "CTI")
public class Address {

    private Integer id;
    private String street;
    private City city;

    @Id
    @GeneratedValue
    @Column(name = "ADD_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "ADD_NM_STREET")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @ManyToOne
    @JoinColumn(name = "CIT_ID", nullable = true)
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String toString() {
        return id + "," + street + ",CITY(" + city + ")";
    }
}
