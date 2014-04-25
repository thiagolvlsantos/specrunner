import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestCore {

    @Test
    public void testLocal() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/core/local.html");
    }

    @Test
    public void testLocalOut() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/core/local.html", "src/test/resources/outcome/core/local.html");
    }

    @Test
    public void testLoop() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/core/loop.html");
    }
}