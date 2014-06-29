package example.sql.reference;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.runner.RunWith;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.listeners.core.PauseOnFailureNodeListener;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultFilter;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Warning;
import org.specrunner.sql.IColumnReader;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.ColumnReaderImpl;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.impl.SqlDumperPrint;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

import example.sql.DataSourceProviderImpl;

@RunWith(SRRunner.class)
public class TestReference {

    @Configuration
    public void configurar(IConfiguration cfg) {
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        cfg.add(PluginConnection.FEATURE_REUSE, true);
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);
        cfg.add(PluginSchema.FEATURE_SOURCE, "/example/sql/reference/value.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);
        cfg.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] { new Database() });
        cfg.add(PluginDatabase.FEATURE_REUSE, true);
        cfg.add(ComparatorDate.FEATURE_TOLERANCE, 5000L);
        // cfg.add(AbstractConverterTimezone.FEATURE_TIMEZONE, "UTC");
        cfg.add(IDatabase.FEATURE_COLUMN_READER, new IColumnReader() {
            private IColumnReader cr = new ColumnReaderImpl();

            @Override
            public Object read(ResultSet rs, Column column) throws SQLException {
                System.out.println("ON:" + column.getName() + ": " + rs.getObject(column.getName()));
                return cr.read(rs, column);
            }
        });

        cfg.add(IDatabase.FEATURE_SQL_DUMPER, new SqlDumperPrint());
        cfg.add(IResultSet.FEATURE_RESULT_FILTER, new IResultFilter() {
            @Override
            public boolean accept(IResult result) {
                return result.getStatus() != Warning.INSTANCE;
            }
        });
        cfg.add(PauseOnFailureNodeListener.FEATURE_PAUSE_ON_FAILURE, true);
        cfg.add(PauseOnFailureNodeListener.FEATURE_SHOW_DIALOG, true);
    }
}