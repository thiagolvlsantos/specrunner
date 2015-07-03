package example.scenarios;

import org.junit.runner.RunWith;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
public class TestScenarioChild extends TestScenarioParent {

    @Override
    @BeforeScenario
    public void increment() {
        size += 2;
        System.out.println(TestScenarioChild.class + ".increment = " + size);
    }

    @BeforeScenario
    public void add() {
        size += 1;
        System.out.println(TestScenarioChild.class + ".add = " + size);
    }

    public boolean sizeIs(int size) {
        return this.size == size;
    }
}