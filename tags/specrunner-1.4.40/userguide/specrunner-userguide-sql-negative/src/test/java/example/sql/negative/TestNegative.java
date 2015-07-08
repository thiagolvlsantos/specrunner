package example.sql.negative;

import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.DatabaseTableEvent;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.negative.PluginDbms;

public class TestNegative extends AbstractTest {

    @Configuration
    public void setDatabase(IConfiguration cfg) {
        cfg.add(PluginDbms.FEATURE_DATABASE, DatabaseLocal.class);
    }

    @SuppressWarnings("serial")
    public static class DatabaseLocal extends DatabaseDefault {
        @Override
        protected void fireTableIn(DatabaseTableEvent event) throws DatabaseException {
            System.out.println("EXTENSION:" + event);
            super.fireTableIn(event);
        }
    }
}