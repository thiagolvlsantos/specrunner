package example.sql.noid;

import org.junit.Test;
import org.specrunner.sql.PluginSchema;

public class TestOrdersSequence extends AbstractTestOrders {

    public TestOrdersSequence() {
        super();
        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/ordersSeq/orders.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
    }

    @Override
    public String getIncome() {
        return "src/test/resources/income/ordersSeq/";
    }

    @Override
    public String getOutcome() {
        return "src/test/resources/outcome/ordersSeq/";
    }

    @Test
    public void runOrdersSequence() {
        run("orders.html");
    }

    @Test
    public void runOrdersSequence2() {
        run("orders.html", "orders2.html");
    }
}