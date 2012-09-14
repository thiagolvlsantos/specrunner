package org.specrunner.sql.proxy;

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
public class WrapperFactoryJdbc implements IWrapperFactory {

    /**
     * Mapping of wrappers.
     */
    protected static Map<Class<?>, String> types = new HashMap<Class<?>, String>();

    static {
        types.put(Driver.class, "org.specrunner.sql.proxy.DriverWrapper");
        types.put(DataSource.class, "org.specrunner.sql.proxy.DataSourceWrapper");
        types.put(Connection.class, "org.specrunner.sql.proxy.ConnectionWrapper");
        types.put(Statement.class, "org.specrunner.sql.proxy.StatementWrapper");
        types.put(PreparedStatement.class, "org.specrunner.sql.proxy.PreparedStatementWrapper");
        types.put(CallableStatement.class, "org.specrunner.sql.proxy.CallableStatementWrapper");
        types.put(ResultSet.class, "org.specrunner.sql.proxy.ResultSetWrapper");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> wrapperClass(Class<T> type) throws Exception {
        String str = types.get(type);
        if (str != null) {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(str);
            for (CtMethod m : cc.getDeclaredMethods()) {
                CtClass[] types = m.getParameterTypes();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < types.length; i++) {
                    sb.append((i == 0 ? "" : ",") + "%s");
                }
                m.addLocalVariable("time", CtClass.longType);
                m.insertBefore("time = System.currentTimeMillis(); System.out.println(\" IN." + m.getName() + "(\" + String.format(\"" + sb + "\",$args)+\")\");");

                // if (m.getReturnType() != CtClass.voidType) {
                m.insertAfter("System.out.println(\"OUT(\"+(System.currentTimeMillis()-time)+\")." + m.getName() + "(" + m.getReturnType().getName() + "):\"+$_);");
                // }
            }
            return cc.toClass();
        }
        return type;
    }
}