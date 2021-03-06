package example.sql.negative;

import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "not equals" }, sorted = true, criteria = StringTestContains.class)
public class TestUsingFile extends AbstractTest {
    // expected to fail with a different register
}
