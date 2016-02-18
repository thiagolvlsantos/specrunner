package example.objects.collections;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestCollection {

    public void showUser(User user) {
        System.out.println(user);
    }

    public void showContact(Contact contact) {
        System.out.println(contact);
    }

    public void showPhone(Phone phone) {
        System.out.println(phone);
    }
}