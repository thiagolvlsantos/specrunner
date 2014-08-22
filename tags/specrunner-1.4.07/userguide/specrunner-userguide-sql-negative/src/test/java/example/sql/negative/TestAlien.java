package example.sql.negative;

import org.specrunner.junit.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "alien" }, sorted = true, criteria = StringTestContains.class)
public class TestAlien extends AbstractTest {
    // expected to fail with an alien register
}