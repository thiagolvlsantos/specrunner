package example.employee;

import org.hibernate.cfg.Configuration;
import org.specrunner.hibernate.IConfigurationProvider;

public class CityConfiguration implements IConfigurationProvider {

    @Override
    public Configuration getConfiguration() throws Exception {
        Configuration ac = new Configuration();
        ac.addAnnotatedClass(City.class);
        ac.addAnnotatedClass(Address.class);
        ac.addAnnotatedClass(Person.class);
        ac.addAnnotatedClass(Contact.class);
        ac.addAnnotatedClass(Employee.class);
        ac.configure("/hibernate.cfg.xml");
        return ac;
    }
}