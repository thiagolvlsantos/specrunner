package example.sql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestOrdersIdentity.class, TestOrdersSequence.class, TestOrdersMix.class })
public class TestOrders {

}