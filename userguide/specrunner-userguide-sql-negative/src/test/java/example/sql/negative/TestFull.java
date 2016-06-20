package example.sql.negative;

import org.specrunner.annotations.Configuration;
import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.result.IResultSet;
import org.specrunner.result.core.StringTestContainsParts;

@ExpectedMessages(value = { "missing|not equals|extra" }, sorted = true, criteria = StringTestContainsParts.class)
public class TestFull extends AbstractTest {
    // expected to fail with an alien, a missing and a different register

    @Configuration
    public void removeFullTrace(IConfiguration cfg) {
        cfg.add(IResultSet.FEATURE_ERROR_MAX_SIZE, null);
        // cfg.add(IResultSet.FEATURE_FULL_TRACE, false);
    }
}
