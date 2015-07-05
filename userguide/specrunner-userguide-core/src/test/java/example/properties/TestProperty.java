package example.properties;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestProperty {

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
        return Arrays.asList(c);
    }
}
