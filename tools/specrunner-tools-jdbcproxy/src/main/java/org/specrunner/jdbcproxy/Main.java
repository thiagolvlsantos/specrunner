package org.specrunner.jdbcproxy;

import org.specrunner.SRServices;
import org.specrunner.jdbcproxy.impl.WrapperFactoryExample;

public class Main {

    static {
        try {
            System.out.println(Main.class + " loading.");
            // DriverManager.setLogWriter(new PrintWriter(System.err));
            SRServices.get().bind(IWrapperFactory.class, new WrapperFactoryExample());
            Class.forName(FactoryJdbcBuilder.class.getName());
            System.out.println(Main.class + " loaded.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            Class.forName(args[0].trim());
        }
    }
}
