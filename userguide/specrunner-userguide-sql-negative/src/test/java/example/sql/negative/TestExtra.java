package example.sql.negative;

import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "extra" }, sorted = true, criteria = StringTestContains.class)
public class TestExtra extends AbstractTest {
    // expected to fail with an alien register
}
