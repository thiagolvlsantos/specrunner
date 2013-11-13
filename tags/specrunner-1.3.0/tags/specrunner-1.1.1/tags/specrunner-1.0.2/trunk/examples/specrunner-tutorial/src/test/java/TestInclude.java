import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.impl.include.PluginInclude;

public class TestInclude {

    @Test
    public void testLocal() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginInclude.FEATURE_EXPANDED, Boolean.TRUE);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/include.html", cfg);
    }
}