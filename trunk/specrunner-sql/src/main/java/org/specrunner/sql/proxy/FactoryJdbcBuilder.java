package org.specrunner.sql.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Wrapped version of a factory.
 * 
 * @author Thiago Santos
 * 
 */
public class FactoryJdbcBuilder implements IFactoryJdbc {

    /**
     * Prefix for wrapper driver.
     */
    private static final String JDBC_SR = "jdbc:sr:";

    /**
     * Unique instance.
     */
    protected static FactoryJdbcBuilder instance = new FactoryJdbcBuilder();

    static {
        try {
            DriverManager.registerDriver(instance.newDriver(JDBC_SR));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the factory instance.
     * 
     * @return The instance.
     */
    public static IFactoryJdbc get() {
        return instance;
    }

    @Override
    public Driver newDriver(String prefix) {
        return new DriverWrapper(this, prefix);
    }

    @Override
    public DataSource newDataSource(DataSource ds) {
        return new DataSourceWrapper(this, ds);
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