package example.sql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.specrunner.junit.concurrent.ConcurrentSuite;

import example.sql.basic.AllBasic;
import example.sql.dbms.AllDbms;
import example.sql.noid.AllOrders;
import example.sql.value.TestDefault;

//CHECKSTYLE:OFF
@RunWith(ConcurrentSuite.class)
@SuiteClasses({ AllBasic.class, AllOrders.class, AllDbms.class, TestDefault.class })
public class TestAll {

}
// CHECKSTYLE:ON
