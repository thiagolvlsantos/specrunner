package example.sql.noid;

import org.joda.time.LocalDate;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.comparators.impl.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

import example.sql.DataSourceProviderImpl;

public abstract class AbstractTestOrders {

    protected IConfiguration cfg;

    public AbstractTestOrders() {
        cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new Database() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        // time comparators tolerance of 1000 milliseconds.
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 1000L);
        // expressions
        SRServices.getExpressionFactory().bindModel("d", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().toString("MM/dd/yyyy");
            }
        });
    }

    public abstract String getIncome();

    public abstract String getOutcome();

    protected void run(String name) {
        run(name, name);
    }

    protected void run(String name, String out) {
        SpecRunnerJUnit.defaultRun(getIncome() + name, getOutcome() + out, cfg);
    }
}