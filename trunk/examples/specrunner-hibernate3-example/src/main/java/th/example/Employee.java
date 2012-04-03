package th.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EMP_EMPLOYEE", schema = "CTI")
public class Employee extends Person {

    private Double salary;

    @Column
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}