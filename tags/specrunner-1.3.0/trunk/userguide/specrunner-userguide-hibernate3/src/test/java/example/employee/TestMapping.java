package example.employee;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestMapping {

    @Test
    public void runCreator() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernateObjectMapping.html");
    }
}
