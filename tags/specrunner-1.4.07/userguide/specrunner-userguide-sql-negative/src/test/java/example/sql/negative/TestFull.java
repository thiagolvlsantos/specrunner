package example.sql.negative;

import org.specrunner.junit.ExpectedMessages;
import org.specrunner.result.core.StringTestContainsParts;

@ExpectedMessages(value = { "missing|different|alien" }, sorted = true, criteria = StringTestContainsParts.class)
public class TestFull extends AbstractTest {
    // expected to fail with an alien, a missing and a different register
}