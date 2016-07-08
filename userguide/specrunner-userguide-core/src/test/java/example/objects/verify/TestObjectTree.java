package example.objects.verify;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestObjectTree {

    public List<User> users() {
        List<User> result = new LinkedList<User>();
        User u = new User("Sérgio", Arrays.asList("sergio@yahoo.com", "sls@gmail.com"), null, null);
        result.add(u);

        u = new User("Auricélia", new LinkedList<String>(), new Address[] { new Address("Condado") }, null);
        result.add(u);

        u = new User("Thiago", Arrays.asList("thiago@yahoo.com", null, "", ""), new Address[] { new Address("Recife") }, Arrays.asList(result.get(0), result.get(1)));
        result.add(u);

        u = new User("", null, null, null);
        result.add(u);

        return result;
    }
}
