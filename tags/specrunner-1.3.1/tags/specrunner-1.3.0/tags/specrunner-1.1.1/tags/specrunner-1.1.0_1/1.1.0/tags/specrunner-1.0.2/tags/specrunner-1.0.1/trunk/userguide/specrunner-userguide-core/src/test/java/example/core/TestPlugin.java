package example.core;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestPlugin {

    @Test
    public void hello1() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hello.html");
    }
}
