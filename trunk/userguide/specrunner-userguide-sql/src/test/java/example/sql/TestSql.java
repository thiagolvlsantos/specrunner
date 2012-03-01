package example.sql;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestSql {

    private static final String INCOME = "src/test/resources/income/full/";
    private static final String OUTCOME = "src/test/resources/outcome/full/";

    private void run(String name) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + name);
    }

    @Test
    public void create() {
        run("create.html");
    }

    @Test
    public void createProvider() {
        run("createProvider.html");
    }

    @Test
    public void drop() {
        run("drop.html");
    }

    @Test
    public void all() {
        run("all.html");
    }
}