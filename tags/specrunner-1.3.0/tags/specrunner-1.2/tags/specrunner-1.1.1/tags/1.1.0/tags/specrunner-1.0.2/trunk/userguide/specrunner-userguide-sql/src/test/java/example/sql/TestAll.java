package example.sql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.specrunner.junit.ConcurrentSuite;

@RunWith(ConcurrentSuite.class)
@SuiteClasses({ TestSql.class, TestSqlFeature.class })
public class TestAll {

}
