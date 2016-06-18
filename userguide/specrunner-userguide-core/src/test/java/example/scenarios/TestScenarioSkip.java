package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.junit.ConditionFalse;
import org.specrunner.junit.SRCondition;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
@SRCondition(ConditionFalse.class)
public class TestScenarioSkip extends TestScenarioParent {
}