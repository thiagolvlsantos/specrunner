package org.specrunner;

import java.io.InputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;

public class JettyMain {

    public static void main(String[] args) throws Exception {
        InputStream config = JettyMain.class.getResourceAsStream("/jetty.xml");
        XmlConfiguration configuration = new XmlConfiguration(config);
        config.close();
        Server server = (Server) configuration.configure();
        server.start();
    }
}
