package org.specrunner;

import org.junit.Test;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.htmlunit.IWait;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestAllSequential extends AbstractTest {

    private static final String INCOME = "src/test/java/org/specrunner/";
    private static final String OUTCOME = "target/all/";

    protected void run(String name) {
        run(name, name);
    }

    protected void run(String name, String out) {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();

        cfg.add(IWait.FEATURE_INTERVAL, 12345L);
        cfg.add(IWait.FEATURE_MAXWAIT, 54321L);

        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + out, cfg);
    }

    @Test
    public void testBasic() {
        run("basic/TestBasic.html");
    }

    @Test
    public void testCheck() {
        run("check/TestCheck.html");
    }

    @Test
    public void testSelect() {
        run("select/TestSelect.html");
    }

    @Test
    public void testTable() {
        run("table/TestTable.html");
    }

    @Test
    public void testNode() {
        run("node/TestNode.html");
    }

    @Test
    public void testText() {
        run("text/TestText.html");
    }
}
