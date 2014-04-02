package org.specrunner;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.context.core.Model;
import org.specrunner.dumper.core.AbstractSourceDumperFile;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.assertions.PluginCompareDate;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

public class TestJetty {

    // static {
    // try {
    // System.out.println("LIGAR PROFILE...");
    // UtilIO.pressKey();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    @Before
    public void prepareTest() throws SpecRunnerException {

        IExpressionFactory ef = SRServices.getExpressionFactory();
        // add predefined objRects that can be used in expressions, and add
        // predefined classes that can be used in expressions, default
        // constructor is invoked.
        ef.bindValue("pattern", "HH:mm:ss").bindClass("dt", DateTime.class).bindModel("ip", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                try {
                    return InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    throw new SpecRunnerException(e);
                }
            }
        }).bindModel("host", Model.of("localhost"));

        // longer tolerance
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PluginCompareDate.FEATURE_TOLERANCE, 60000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }

    @Test
    public void runJettyChrome() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryChrome.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jetty-crome.html", cfg);
    }

    @Test
    public void runJettyIe() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
        cfg.add(WebDriverFactoryIe.FEATURE_DRIVER, "C:\\Users\\Thiago\\Downloads\\IEDriverServer.exe");
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jetty-ie.html", cfg);
    }

    @Test
    public void runJettyFire() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false).add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, FirefoxDriver.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jetty-fire.html", cfg);
    }

    public void runJetty(IConfiguration cfg) throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", cfg);
    }

    @Test
    public void runJettyWithRecording() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html");
    }

    @Test
    public void runWithoutScreenCapture() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        // disable recording browser information change output from
        // default(example-jetty.html) to another file.
        cfg.add(PluginBrowser.FEATURE_RECORDING, false).add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording.html");
        // same as before.
        runJetty(cfg);
    }

    @Test
    public void runWithoutScreenCapture2() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        // disable recording browser information change output from
        // default(example-jetty.html) to another file.
        cfg.add(PluginBrowser.FEATURE_RECORDING, false).add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording2.html");
        // same as before.
        runJetty(cfg);
    }

    @Test
    public void runWithoutScreenCapture3() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        // disable recording browser information change output from
        // default(example-jetty.html) to another file.
        cfg.add(PluginBrowser.FEATURE_RECORDING, false).add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording3.html");
        // same as before.
        runJetty(cfg);
    }

    @Test
    public void runTwice() throws Exception {
        // run again to check time performance after classes are loaded.
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-one.html");
        runJetty(cfg);

        // run again to check time performance after classes are loaded.
        cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false).add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-two.html");
        runJetty(cfg);
    }

    public static void main(String[] args) throws Exception {
        TestJetty tj = new TestJetty();
        tj.prepareTest();
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PluginBrowser.FEATURE_RECORDING, false);
        fm.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, new File("src/test/resources/outcome/run"));
        for (int j = 0; j < 10000; j++) {
            try {
                fm.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-run.html");
                tj.runJettyWithRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}