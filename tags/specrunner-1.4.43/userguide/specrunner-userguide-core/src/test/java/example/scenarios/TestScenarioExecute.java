package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
public class TestScenarioExecute {

    @Configuration
    public void cfg(IConfiguration cfg) {
        // cfg.add(ScenarioFrameListener.FEATURE_EXECUTE_ENABLED, false);
    }
}
