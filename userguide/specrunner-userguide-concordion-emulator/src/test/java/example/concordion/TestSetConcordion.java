package example.concordion;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@SuppressWarnings("serial")
@RunWith(SRRunner.class)
public class TestSetConcordion {

    public String greetingFor(String firstName) {
        return "Hello " + firstName + "!";
    }
}