package example.scenarios;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.annotations.SRRunnerCondition;
import org.specrunner.annotations.core.SkipTrue;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
@SRRunnerCondition(SkipTrue.class)
public class TestScenarioSkip extends TestScenarioParent {

    @Before
    public void done() {
    }
}