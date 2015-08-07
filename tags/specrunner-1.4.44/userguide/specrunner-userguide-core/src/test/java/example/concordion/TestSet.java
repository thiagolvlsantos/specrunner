package example.concordion;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSet {

    public String greetingFor(String firstName) {
        return "Hello " + firstName + "!";
    }
}
