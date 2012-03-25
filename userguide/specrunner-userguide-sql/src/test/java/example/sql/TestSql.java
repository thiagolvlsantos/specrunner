package example.sql;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestSql {

    private static final String INCOME = "src/test/resources/income/full/";
    private static final String OUTCOME = "src/test/resources/outcome/full/";

    private void run(String name) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + name);
    }

    @Before
    public void before() {
        IExpressionFactory ief = SpecRunnerServices.get(IExpressionFactory.class);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        ief.bindPredefinedValue("pattern", pattern);
        ief.bindPredefinedValue("time", "{ts '" + new DateTime().toString(pattern) + "'}");
        ief.bindPredefinedClass("dt", DateTime.class);
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