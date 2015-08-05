package example.sql.noid;

import java.util.Arrays;

import org.junit.Test;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabasePrintListener;

public class TestOrdersIdentity extends AbstractTestOrders {

    public TestOrdersIdentity() {
        super();
        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/orders/orders.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        // cfg.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new
        // DatabaseCountListener()));
        cfg.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new DatabasePrintListener()));
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
}
