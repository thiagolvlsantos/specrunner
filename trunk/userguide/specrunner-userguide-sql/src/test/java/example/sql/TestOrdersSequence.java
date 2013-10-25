package example.sql;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.impl.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

public class TestOrdersSequence {

    private static final String INCOME = "src/test/resources/income/ordersSeq/";
    private static final String OUTCOME = "src/test/resources/outcome/ordersSeq/";

    protected IConfiguration cfg;

    public TestOrdersSequence() {
    }

    @Before
    public void before() {
        cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/ordersSeq/ordersSeq.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new Database() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        // time comparators tolerance of 1000 milliseconds.
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 1000L);

        // expressions
        SpecRunnerServices.get(IExpressionFactory.class).bindValue("d", new LocalDate().toString("MM/dd/yyyy"));
    }

    protected void run(String name) {
        run(name, name);
    }

    protected void run(String name, String out) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + out, cfg);
    }

    @Test
    public void runOrdersSequence() {
        run("ordersSeq.html");
    }

    @Test
    public void runOrdersSequence2() {
        run("ordersSeq.html", "ordersSeq2.html");
    }
}