package example.sql.nullhandler;

import org.junit.runner.RunWith;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.IDatabaseNullEmpty;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.database.impl.NullEmptyHandlerDefault;
import org.specrunner.sql.meta.EMode;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

import example.sql.DataSourceProviderImpl;

@RunWith(SRRunner.class)
@ExpectedMessages("Values are different.\n(expected        )null\n(received        )\n(expected aligned)null\n(received aligned)----")
public class TestNull {

    @Configuration
    public void configurar(IConfiguration cfg) {
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginSchema.FEATURE_SOURCE, "/example/sql/nullhandler/value.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new DatabaseDefault() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 5000L);
        cfg.add(IDatabaseNullEmpty.FEATURE_NULL_EMPTY_HANDLER, new NullEmptyHandlerDefault() {
            @Override
            public boolean isNull(EMode mode, String value) {
                boolean tmp = super.isNull(mode, value);
                System.out.println("isNull(" + value + "," + mode + ") = " + tmp);
                return tmp;
            }
        });
    }
}