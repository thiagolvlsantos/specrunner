package example.sql.log;

import org.junit.runner.RunWith;
import org.specrunner.annotations.Configuration;
import org.specrunner.comparators.core.AbstractComparatorTime;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.SRRunner;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

import example.sql.DataSourceProviderImpl;

@RunWith(SRRunner.class)
public class TestLog2 {

    @Configuration
    public void configurar(IConfiguration cfg) {
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new DatabaseDefault() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        cfg.add(PluginSchema.FEATURE_SOURCE, "/example/sql/log/orders2.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        // 2 seconds of tolerance
        cfg.add(AbstractComparatorTime.FEATURE_TOLERANCE, 2 * 1000L);
    }
}
