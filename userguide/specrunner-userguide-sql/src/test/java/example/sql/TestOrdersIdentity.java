package example.sql;

import org.junit.Test;
import org.specrunner.sql.PluginSchema;

public class TestOrdersIdentity extends AbstractTestOrders {

    public TestOrdersIdentity() {
        super();
        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/orders/orders.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
    }

    @Override
    public String getIncome() {
        return "src/test/resources/income/orders/";
    }

    @Override
    public String getOutcome() {
        return "src/test/resources/outcome/orders/";
    }

    @Test
    public void runOrdersIdentity() {
        run("orders.html");
    }

    @Test
    public void runOrdersIdentity2() {
        run("orders.html", "orders2.html");
    }
}