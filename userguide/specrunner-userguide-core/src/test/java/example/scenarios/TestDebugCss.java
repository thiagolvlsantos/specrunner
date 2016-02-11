package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.source.resource.IResourceManager;

@RunWith(SRRunnerScenario.class)
public class TestDebugCss {

    @Configuration
    public void config(IConfiguration cfg) {
        cfg.add(IResourceManager.FEATURE_ADD_DEBUG_CSS, true);
    }
}