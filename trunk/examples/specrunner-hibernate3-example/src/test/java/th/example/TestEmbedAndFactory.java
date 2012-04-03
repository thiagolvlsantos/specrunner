package th.example;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestEmbedAndFactory {

    @Test
    public void rodarShort() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernate_short.html");
    }

    @Test
    public void rodarFactory() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernate_factory.html");
    }
}