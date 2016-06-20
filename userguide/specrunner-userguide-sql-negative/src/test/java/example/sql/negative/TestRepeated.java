package example.sql.negative;

import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.result.core.StringTestContains;

@ExpectedMessages(value = { "Error in datasource: systemConnection", "Error in datasource: referenceConnection" }, sorted = true, criteria = StringTestContains.class)
public class TestRepeated extends AbstractTest {
    // expected to fail with a repeated register
}
