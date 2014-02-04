package org.specrunner.jdbcproxy;

import org.specrunner.SpecRunnerServices;
import org.specrunner.jdbcproxy.impl.WrapperFactoryExample;

public class Main {

    static {
        try {
            System.out.println("org.specrunner.jdbcproxy loading.");
            // DriverManager.setLogWriter(new PrintWriter(System.err));
            SpecRunnerServices.get().bind(IWrapperFactory.class, new WrapperFactoryExample());
            Class.forName(FactoryJdbcBuilder.class.getName());
            System.out.println("org.specrunner.jdbcproxy loaded.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }
}
