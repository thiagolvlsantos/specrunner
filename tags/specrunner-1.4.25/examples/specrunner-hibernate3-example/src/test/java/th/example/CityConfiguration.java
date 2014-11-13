package th.example;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.specrunner.hibernate3.IConfigurationProvider;

public class CityConfiguration implements IConfigurationProvider {

    @Override
    public Configuration getConfiguration() throws Exception {
        AnnotationConfiguration ac = new AnnotationConfiguration();
        ac.addAnnotatedClass(City.class);
        ac.addAnnotatedClass(Address.class);
        ac.addAnnotatedClass(Person.class);
        ac.addAnnotatedClass(Contact.class);
        ac.addAnnotatedClass(Employee.class);
        ac.configure("/hibernate.cfg.xml");
        return ac;
    }

    public static void main(String[] args) throws Exception {
        new CityConfiguration().getConfiguration();
    }

}
