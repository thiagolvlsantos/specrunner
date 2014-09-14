package example.sql.negative;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestMissing.class, TestNegative.class, TestExtra.class })
public class SuiteContext {
}