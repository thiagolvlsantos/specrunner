package example.sql.dbms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestDbms.class, TestDbmsFeature.class, TestDbmsNegative.class, TestDbmsNegativeFeature.class })
public class AllDbms {

}
