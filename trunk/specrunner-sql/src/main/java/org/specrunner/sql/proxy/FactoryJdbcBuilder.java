package org.specrunner.sql.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Wrapped version of a factory.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("unchecked")
public class FactoryJdbcBuilder implements IFactoryJdbc {

    /**
     * Prefix for wrapper driver.
     */
    public static final String JDBC_SR = "jdbc:sr:";

    /**
     * Unique instance.
     */
    protected static FactoryJdbcBuilder instance = new FactoryJdbcBuilder();

    /**
     * Default implementation of a wrapper.
     */
    protected IWrapperFactory classFactory;

    /**
     * The JDBC wrapper class.
     */
    protected Class<Driver> driverWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<DataSource> dataSourceWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<Connection> connectionWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<Statement> statementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<PreparedStatement> preparedStatementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<CallableStatement> callableStatementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected Class<ResultSet> resultSetWrapper;

    static {
        try {
            DriverManager.registerDriver(instance.newDriver(JDBC_SR));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Default constructor.
     */
    public FactoryJdbcBuilder() {
        try {
            if (classFactory == null) {
                classFactory = new WrapperFactoryJdbc();
            }
            driverWrapper = classFactory.wrapperClass(Driver.class);
            dataSourceWrapper = classFactory.wrapperClass(DataSource.class);
            connectionWrapper = classFactory.wrapperClass(Connection.class);
            statementWrapper = classFactory.wrapperClass(Statement.class);
            preparedStatementWrapper = classFactory.wrapperClass(PreparedStatement.class);
            callableStatementWrapper = classFactory.wrapperClass(CallableStatement.class);
            resultSetWrapper = classFactory.wrapperClass(ResultSet.class);
        } catch (Exception e) {
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
        try {
            return driverWrapper.getConstructor(IFactoryJdbc.class, String.class).newInstance(this, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataSource newDataSource(DataSource ds) {
        try {
            return dataSourceWrapper.getConstructor(IFactoryJdbc.class, DataSource.class).newInstance(this, ds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection newConnection(Connection con) {
        try {
            return connectionWrapper.getConstructor(IFactoryJdbc.class, Connection.class).newInstance(this, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Statement newStatement(Statement stmt) {
        try {
            return statementWrapper.getConstructor(IFactoryJdbc.class, Statement.class).newInstance(this, stmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PreparedStatement newPreparedStatement(PreparedStatement pstmt) {
        try {
            return preparedStatementWrapper.getConstructor(IFactoryJdbc.class, PreparedStatement.class).newInstance(this, pstmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CallableStatement newCallableStatement(CallableStatement cstmt) {
        try {
            return callableStatementWrapper.getConstructor(IFactoryJdbc.class, CallableStatement.class).newInstance(this, cstmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet newResultSet(ResultSet rs) {
        try {
            return resultSetWrapper.getConstructor(IFactoryJdbc.class, ResultSet.class).newInstance(this, rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}