package org.specrunner.sql.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

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
    private static final String JDBC_SR = "jdbc:sr:";

    /**
     * Unique instance.
     */
    protected static FactoryJdbcBuilder instance = new FactoryJdbcBuilder();

    /**
     * The JDBC wrapper class.
     */
    protected static Class<Driver> driverWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<DataSource> dataSourceWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<Connection> connectionWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<Statement> statementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<PreparedStatement> preparedStatementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<CallableStatement> callableStatementWrapper;
    /**
     * The JDBC wrapper class.
     */
    protected static Class<ResultSet> resultSetWrapper;

    static {
        try {
            driverWrapper = (Class<Driver>) instrument("org.specrunner.sql.proxy.DriverWrapper");
            dataSourceWrapper = (Class<DataSource>) instrument("org.specrunner.sql.proxy.DataSourceWrapper");
            connectionWrapper = (Class<Connection>) instrument("org.specrunner.sql.proxy.ConnectionWrapper");
            statementWrapper = (Class<Statement>) instrument("org.specrunner.sql.proxy.StatementWrapper");
            preparedStatementWrapper = (Class<PreparedStatement>) instrument("org.specrunner.sql.proxy.PreparedStatementWrapper");
            callableStatementWrapper = (Class<CallableStatement>) instrument("org.specrunner.sql.proxy.CallableStatementWrapper");
            resultSetWrapper = (Class<ResultSet>) instrument("org.specrunner.sql.proxy.ResultSetWrapper");
            DriverManager.registerDriver(instance.newDriver(JDBC_SR));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Perform instrumentation of classes.
     * 
     * @param clazz
     *            The class name.
     * @return The modified class.
     * @throws Exception
     *             On instrumentation errors.
     */
    protected static Class<?> instrument(String clazz) throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(clazz);
        for (CtMethod m : cc.getDeclaredMethods()) {
            CtClass[] types = m.getParameterTypes();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < types.length; i++) {
                sb.append((i == 0 ? "" : ",") + "%s");
            }
            m.insertBefore("System.out.println(\"" + m.getName() + "->\" + String.format(\"" + sb + "\",$args));");
        }
        return cc.toClass();
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