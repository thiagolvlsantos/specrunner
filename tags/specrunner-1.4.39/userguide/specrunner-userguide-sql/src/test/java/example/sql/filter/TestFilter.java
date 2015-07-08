package example.sql.filter;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.context.IContext;
import org.specrunner.expressions.EMode;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginFilter;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.database.impl.DatabasePrintListener;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IDataFilter;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

import example.sql.DataSourceProviderImpl;

@RunWith(SRRunnerScenario.class)
public class TestFilter {

    public static class FilterTest implements IDataFilter {
        @Override
        public void setup(IContext context, EMode mode, Schema schema) throws PluginException {
        }

        @Override
        public boolean accept(EMode mode, Column column, Object value) {
            Table table = column.getParent();
            return !(table.getAlias().equalsIgnoreCase("tasks") && column.getAlias().equalsIgnoreCase("time") && value == null);
        }

        @Override
        public boolean accept(EMode mode, Column column) {
            Table table = column.getParent();
            return !(table.getAlias().equalsIgnoreCase("projects") && column.getAlias().equalsIgnoreCase("description"));
        }

        @Override
        public boolean accept(EMode mode, IRegister register) {
            Table table = register.getParent();
            if (table.getAlias().equalsIgnoreCase("items")) {
                Value left = register.getByAlias("a");
                Value right = register.getByAlias("b");
                return "1".equals(String.valueOf(left.getValue())) && "1".equals(String.valueOf(right.getValue()));
            }
            return true;
        }

        @Override
        public boolean accept(EMode mode, Table table) {
            return !table.getAlias().equalsIgnoreCase("users");
        }

        @Override
        public boolean accept(EMode mode, Schema schema) {
            return true;
        }
    }

    @Configuration
    public void configurar(IConfiguration cfg) {
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginSchema.FEATURE_SOURCE, "/example/sql/filter/value.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new DatabaseDefault() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 5000L);

        cfg.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new DatabasePrintListener()));

        cfg.add(PluginFilter.FEATURE_FILTER, FilterTest.class.getName());
    }
}