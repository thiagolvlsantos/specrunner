package th.example;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestSql {

    @BeforeClass
    public static void beforeClass() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/schema.html", "src/test/resources/outcome/schema.html");
    }

    @Before
    public void before() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/drop.html", "src/test/resources/outcome/drop.html");
        SpecRunnerJUnit.defaultRun("src/test/resources/income/schema.html", "src/test/resources/outcome/schema.html");
    }

    protected void run(int index) {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/sql.html", "src/test/resources/outcome/sql" + index + ".html");
    }

    @Test
    public void rodarSql1() throws Exception {
        run(1);
    }

    @Test
    public void rodarSql2() throws Exception {
        run(2);
    }
}
