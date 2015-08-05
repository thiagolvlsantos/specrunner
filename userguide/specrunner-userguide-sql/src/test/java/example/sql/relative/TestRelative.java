package example.sql.relative;

import org.junit.Test;

import example.sql.noid.AbstractTestOrders;

public class TestRelative extends AbstractTestOrders {

    @Override
    public String getIncome() {
        return "src/test/resources/income/relative/";
    }

    @Override
    public String getOutcome() {
        return "src/test/resources/outcome/relative/";
    }

    @Test
    public void runRelative() {
        run("relative.html");
    }

    @Test
    public void runRelative2() {
        run("relative.html");
    }
}
