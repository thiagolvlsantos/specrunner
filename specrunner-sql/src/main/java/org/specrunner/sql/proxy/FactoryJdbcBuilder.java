package org.specrunner.sql.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Wrapped version of a factory.
 * 
 * @author Thiago Santos
 * 
 */
public class FactoryJdbcBuilder implements IFactoryJdbc {

    static {
        try {
            FactoryJdbcBuilder factory = new FactoryJdbcBuilder();
            DriverManager.registerDriver(new DriverWrapper(factory));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection newConnection(Connection con) {
        return new ConnectionWrapper(this, con);
    }

    @Override
    public Statement newStatement(Statement stmt) {
        return new StatementWrapper(this, stmt);
    }

    @Override
    public PreparedStatement newPreparedStatement(PreparedStatement pstmt) {
        return new PreparedStatementWrapper(this, pstmt);
    }

    @Override
    public CallableStatement newCallableStatement(CallableStatement cstmt) {
        return new CallableStatementWrapper(this, cstmt);
    }

    @Override
    public ResultSet newResultSet(ResultSet rs) {
        return new ResultSetWrapper(this, rs);
    }
}