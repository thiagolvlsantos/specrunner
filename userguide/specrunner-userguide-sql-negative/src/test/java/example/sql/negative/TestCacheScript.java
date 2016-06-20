package example.sql.negative;

import org.specrunner.annotations.Configuration;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.sql.database.impl.DatabaseDefault;

public class TestCacheScript extends AbstractTest {

    @Configuration
    public void useMd5(IConfiguration cfg) {
        cfg.add(DatabaseDefault.FEATURE_MD5_KEYS, Boolean.TRUE);
    }
}
