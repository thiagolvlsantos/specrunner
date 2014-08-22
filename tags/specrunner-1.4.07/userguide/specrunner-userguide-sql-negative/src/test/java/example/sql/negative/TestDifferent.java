package example.sql.negative;

import org.specrunner.junit.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "different" }, sorted = true, criteria = StringTestContains.class)
public class TestDifferent extends AbstractTest {
    // expected to fail with a different register
}