package example.sql.negative;

import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "missing" }, sorted = true, criteria = StringTestContains.class)
public class TestMissing extends AbstractTest {
    // expected to fail with a missing register
}
