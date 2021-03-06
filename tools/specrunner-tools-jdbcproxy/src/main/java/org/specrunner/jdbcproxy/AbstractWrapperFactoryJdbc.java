package org.specrunner.jdbcproxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.sql.DataSource;

/**
 * Example of wrapper factory.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractWrapperFactoryJdbc implements IWrapperFactory {

    /**
     * Mapping of wrappers.
     */
    protected Map<Class<?>, String> types = new HashMap<Class<?>, String>();

    /**
     * Default constructor.
     */
    public AbstractWrapperFactoryJdbc() {
        types.put(Driver.class, "org.specrunner.jdbcproxy.jdbc.DriverWrapper");
        types.put(DataSource.class, "org.specrunner.jdbcproxy.jdbc.DataSourceWrapper");
        types.put(Connection.class, "org.specrunner.jdbcproxy.jdbc.ConnectionWrapper");
        types.put(Statement.class, "org.specrunner.jdbcproxy.jdbc.StatementWrapper");
        types.put(PreparedStatement.class, "org.specrunner.jdbcproxy.jdbc.PreparedStatementWrapper");
        types.put(CallableStatement.class, "org.specrunner.jdbcproxy.jdbc.CallableStatementWrapper");
        types.put(ResultSet.class, "org.specrunner.jdbcproxy.jdbc.ResultSetWrapper");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> wrapperClass(Class<T> type) throws Exception {
        String str = types.get(type);
        if (str != null) {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(str);
            for (CtMethod m : cc.getDeclaredMethods()) {
                wrapperMethod(type, m);
            }
            return cc.toClass();
        }
        return type;
    }

    /**
     * Wrap a method.
     * 
     * @param <T>
     *            The object type.
     * @param type
     *            The type.
     * @param m
     *            The method.
     * @throws Exception
     *             On wrapper error.
     */
    protected abstract <T> void wrapperMethod(Class<T> type, CtMethod m) throws Exception;
}
