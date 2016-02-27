package example.sql.negative;

import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.sql.negative.DatabaseScenarioDbmsListener;

@SRScenarioListeners(inheritListeners = false, value = { DatabaseScenarioDbmsListener.class })
public class TestInheritanceStart extends AbstractTest {
}
