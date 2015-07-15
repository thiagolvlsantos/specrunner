package example.properties;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.util.xom.node.INodeHolder;

@RunWith(SRRunner.class)
public class TestProperty {

    @Configuration
    public void config(IConfiguration cfg) {
        cfg.add(INodeHolder.FEATURE_PROPERTY_INVALID_PATH_AS_NULL, Boolean.TRUE);
        // cfg.add(INodeHolder.FEATURE_PROPERTY_ACCEPT_NULL_PATH,
        // Boolean.FALSE);
    }

    public List<Contact> full() {
        Contact c = new Contact();
        c.setName("Me");
        c.setExpire(new DateTime().plusDays(10));
        c.setAddress(new Address());
        c.getAddress().setStreet("Ocean");
        return Arrays.asList(c);
    }

    public List<Contact> partial() {
        Contact c = new Contact();
        c.setName("Myself");
        c.setData(new LocalDate(2015, 10, 15));
        Contact c2 = new Contact();
        c2.setName("AndI");
        c2.setData(LocalDate.now());
        return Arrays.asList(c, c2);
    }
}
