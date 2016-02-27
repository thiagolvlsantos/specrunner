package example.sql.negative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultFilter;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Warning;
import org.specrunner.sql.database.IColumnReader;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.ColumnReaderDefault;
import org.specrunner.sql.database.impl.DatabasePrintListener;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.negative.DatabaseScenarioListener;

@RunWith(SRRunnerScenario.class)
@SRScenarioListeners(DatabaseScenarioListener.class)
public abstract class AbstractTest {

    @Configuration
    public void configureCache(IConfiguration cfg) {
        cfg.add(IDatabase.FEATURE_REUSE_SCRIPTS, true);
    }

    @Configuration
    public void configureFilter(IConfiguration cfg) {
        cfg.add(IResultSet.FEATURE_RESULT_FILTER, new IResultFilter() {

            @Override
            public boolean accept(IResult result) {
                return result.getStatus() != Warning.INSTANCE;
            }
        });
    }

    @Configuration
    public void configureListeners(IConfiguration cfg) {
        cfg.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new DatabasePrintListener()));
    }

    @Configuration
    public void configureReader(IConfiguration cfg) {
        cfg.add(IDatabase.FEATURE_COLUMN_READER, new IColumnReader() {
            private IColumnReader cr = new ColumnReaderDefault();

            @Override
            public Object read(ResultSet rs, Column column) throws SQLException {
                System.out.println("ON:" + column.getName() + ": " + rs.getObject(column.getName()));
                return cr.read(rs, column);
            }
        });
    }

}
