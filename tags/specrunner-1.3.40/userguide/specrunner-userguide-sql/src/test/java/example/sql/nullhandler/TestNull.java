package example.sql.nullhandler;

import org.junit.runner.RunWith;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;
import org.specrunner.sql.EMode;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.impl.NullEmptyHandlerImpl;
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
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new Database() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 5000L);
        cfg.add(IDatabase.FEATURE_NULL_EMPTY_HANDLER, new NullEmptyHandlerImpl() {
            @Override
            public boolean isNull(String value, EMode mode) {
                boolean tmp = super.isNull(value, mode);
                System.out.println("isNull(" + value + "," + mode + ") = " + tmp);
                return tmp;
            }
        });
    }
}