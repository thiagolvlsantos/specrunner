package example.sql.negative;

import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.sql.negative.DatabaseScenarioCompareListener;

@SRScenarioListeners(inheritListeners = false, value = { DatabaseScenarioCompareListener.class })
public class TestInheritance extends AbstractTest {
}