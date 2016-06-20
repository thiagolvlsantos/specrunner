package example.ant;

import org.junit.runner.RunWith;
import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
@ExpectedMessages(value = { "Target \"messageK\" does not exist in the project \"failproject\". It is used from target \"test\"." }, sorted = true)
public class TestAnt {
}
