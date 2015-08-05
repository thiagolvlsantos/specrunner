package example.sql.noid;

import org.junit.Test;
import org.specrunner.sql.PluginSchema;

public class TestOrdersMix extends AbstractTestOrders {

    public TestOrdersMix() {
        super();
        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/ordersMix/orders.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
    }

    @Override
    public String getIncome() {
        return "src/test/resources/income/ordersMix/";
    }

    @Override
    public String getOutcome() {
        return "src/test/resources/outcome/ordersMix/";
    }

    @Test
    public void runOrdersMix() {
        run("orders.html");
    }
}
