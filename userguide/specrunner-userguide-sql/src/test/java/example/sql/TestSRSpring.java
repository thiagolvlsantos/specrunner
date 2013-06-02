package example.sql;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerSpring;
import org.springframework.test.context.TestExecutionListeners;

@RunWith(SRRunnerSpring.class)
@TestExecutionListeners(inheritListeners = false, listeners = { PrintListener.class })
public class TestSRSpring {
}