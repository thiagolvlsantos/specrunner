package example.jdbcproxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.Test;
import org.specrunner.jdbcproxy.Main;

public class TestDriver {

    @Test
    public void test() throws Exception {
        Main.main(new String[] { "org.hsqldb.jdbcDriver" });
        Connection con = DriverManager.getConnection("jdbc:sr:jdbc:hsqldb:mem:TESTE");
        Statement stmt = con.createStatement();
        stmt.execute("CREATE SCHEMA TES authorization DBA");
        stmt.close();
        con.close();
    }
}
