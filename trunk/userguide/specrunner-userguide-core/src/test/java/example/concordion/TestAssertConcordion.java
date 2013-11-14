package example.concordion;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestAssertConcordion extends Concordion {

    public String getGreeting() {
        return "Hello World!";
    }
}
