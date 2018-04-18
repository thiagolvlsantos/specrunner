package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.annotations.SRRunnerCondition;
import org.specrunner.annotations.core.SkipTrue;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
@SRRunnerCondition(SkipTrue.class)
public class TestScenarioSkipScenarios extends TestScenarioParent {
}
