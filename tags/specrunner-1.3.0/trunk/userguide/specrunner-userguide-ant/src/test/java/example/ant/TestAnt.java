package example.ant;

import org.junit.runner.RunWith;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
@ExpectedMessages({ "Target \"copyK\" does not exist in the project \"testproject\". It is used from target \"test\"." })
public class TestAnt {
}