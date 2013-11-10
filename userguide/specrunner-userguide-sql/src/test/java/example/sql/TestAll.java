package example.sql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.specrunner.junit.ConcurrentSuite;

import example.sql.basic.AllBasic;
import example.sql.dbms.AllDbms;
import example.sql.noid.AllOrders;

//CHECKSTYLE:OFF
@RunWith(ConcurrentSuite.class)
@SuiteClasses({ AllBasic.class, AllOrders.class, AllDbms.class })
public class TestAll {

}
// CHECKSTYLE:ON
