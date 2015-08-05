package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
public abstract class TestScenarioParent {

    protected int size = 0;

    @BeforeScenario
    public void increment() {
        size += 1;
        System.out.println(TestScenarioParent.class + ".increment = " + size);
    }
}
