package example.objects.verify;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestObjectTree {

    public List<User> users() {
        List<User> result = new LinkedList<User>();
        User u = new User("Sérgio", new LocalDate(1954, 03, 20), Arrays.asList("sergio@yahoo.com", "sls@gmail.com"), new Address("Condado"), null, null);
        result.add(u);

        u = new User("Auricélia", new LocalDate(1953, 12, 23), new LinkedList<String>(), null, new Address[] { new Address("Condado") }, null);
        result.add(u);

        u = new User("Thiago", new LocalDate(), Arrays.asList("thiago@yahoo.com", null, "", null), new Address("Recife"), new Address[] { new Address("Recife") }, Arrays.asList(result.get(0), result.get(1)));
        result.add(u);

        u = new User("", null, null, null, null, null);
        result.add(u);

        return result;
    }

    public User[] call() {
        return new User[] { users().get(0) };
    }
}
