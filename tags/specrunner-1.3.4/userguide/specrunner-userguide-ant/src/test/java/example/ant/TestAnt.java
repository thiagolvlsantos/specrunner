package example.ant;

import org.junit.runner.RunWith;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
@ExpectedMessages({ "Target \"messageK\" does not exist in the project \"failproject\". It is used from target \"test\"." })
public class TestAnt {
}