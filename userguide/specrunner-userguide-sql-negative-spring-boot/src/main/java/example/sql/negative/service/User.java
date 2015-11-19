package example.sql.negative.service;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "SYS", name = "USR_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "USR_ID")
    private Long id;

    @Column(name = "USR_NAME")
    @NotNull
    private String name;

    @Column(name = "USR_BIRTHDAY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    public User(String name) {
        this.name = name;
    }
}