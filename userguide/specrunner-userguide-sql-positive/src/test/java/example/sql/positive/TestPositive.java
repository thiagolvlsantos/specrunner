package example.sql.positive;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.specrunner.annotations.Configuration;
import org.specrunner.annotations.SRScenarioListeners;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultFilter;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Warning;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabasePrintListener;
import org.specrunner.sql.positive.DatabaseScenarioListener;

@RunWith(SRRunnerScenario.class)
@SRScenarioListeners(DatabaseScenarioListener.class)
public class TestPositive {

    @Configuration
    public void configureCache(IConfiguration cfg) {
        cfg.add(IDatabase.FEATURE_REUSE_SCRIPTS, true);
    }

    @Configuration
    public void config(IConfiguration cfg) {
        cfg.add(IResultSet.FEATURE_RESULT_FILTER, new IResultFilter() {

            @Override
            public boolean accept(IResult result) {
                return result.getStatus() != Warning.INSTANCE;
            }
        });
        cfg.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new DatabasePrintListener()));
    }
}
