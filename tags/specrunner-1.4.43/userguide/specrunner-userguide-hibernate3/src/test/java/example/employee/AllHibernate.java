package example.employee;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCreator.class, TestHibernate.class, TestMapping.class })
public class AllHibernate {

}
