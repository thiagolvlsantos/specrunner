package org.specrunner.application;

import java.io.InputStream;
import java.net.Socket;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Start extends Thread {

    private static Object lock = new Object();

    public static void main(String[] args) throws Exception {
        new Start().start();
        new Start().start();
    }

    @Override
    public void run() {
        try {
            Server server = new Server();
            synchronized (lock) {
                SocketConnector connector = new SocketConnector();

                // Set some timeout options to make debugging easier.
                connector.setMaxIdleTime(1000 * 60 * 60);
                connector.setSoLingerTime(-1);

                int port = 8080;
                boolean available = false;
                int tries = 1000;
                InputStream in = null;
                for (int i = port; !available && i < port + tries; i++) {
                    Socket sock = null;
                    try {
                        sock = new Socket("localhost", i);
                        in = sock.getInputStream();
                    } catch (Exception e) {
                        port = i;
                        available = true;
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e) {
                            }
                        }
                        if (sock != null) {
                            try {
                                sock.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                connector.setPort(port);
                server.setConnectors(new Connector[] { connector });

                WebAppContext bb = new WebAppContext();
                bb.setServer(server);
                bb.setContextPath("/");
                bb.setWar("src/main/webapp");

                // START JMX SERVER
                // MBeanServer mBeanServer =
                // ManagementFactory.getPlatformMBeanServer();
                // MBeanContainer mBeanContainer = new
                // MBeanContainer(mBeanServer);
                // server.getContainer().addEventListener(mBeanContainer);
                // mBeanContainer.start();

                server.addHandler(bb);

                System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
                server.start();
                while (!server.isStarted()) {
                    System.out.println("Esperando...");
                    Thread.sleep(500);
                }
            }
            System.in.read();
            System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
            // while (System.in.available() == 0) {
            // Thread.sleep(5000);
            // }
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
