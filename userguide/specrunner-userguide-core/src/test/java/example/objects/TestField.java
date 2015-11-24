package example.objects;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerScenario;

import example.objects.fields.User;

@RunWith(SRRunnerScenario.class)
public class TestField {

    public void callUser(User user) {
        System.out.println(user);
    }
}