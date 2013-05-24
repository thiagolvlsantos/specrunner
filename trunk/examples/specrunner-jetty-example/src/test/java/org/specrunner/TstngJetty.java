package org.specrunner;

import java.io.File;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.result.IResultSet;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.assertions.PluginCompareDate;
import org.specrunner.webdriver.impl.FinderXPath;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TstngJetty {

    @BeforeMethod
    public void prepareTest() throws SpecRunnerException {
        IExpressionFactory ef = SpecRunnerServices.get(IExpressionFactory.class);
        // add predefined objects that can be used in expressions
        ef.bindValue("pattern", "HH:mm:ss");
        // add predefined classes that can be used in expressions, default
        // constructor is invoked.
        ef.bindClass("dt", DateTime.class);

        // longer tolerance
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginCompareDate.FEATURE_TOLERANCE, 5000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runJetty() throws Exception {
        try {
            ISpecRunner dr = SpecRunnerServices.getSpecRunner();
            IResultSet res = dr.run("src/test/resources/income/example-jetty.html");
            Assert.assertTrue(res.asString(), !res.getStatus().isError());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runWithoutScreenCapture() throws Exception {
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        // disable recording browser information.
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        // change output from default(example-jetty.html) to another file.
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording.html");

        // same as before.
        runJetty();
    }

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runWithoutScreenCapture2() throws Exception {
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        // disable recording browser information.
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        // change output from default(example-jetty.html) to another file.
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording2.html");

        // same as before.
        runJetty();
    }

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runWithoutScreenCapture3() throws Exception {
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        // disable recording browser information.
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        // change output from default(example-jetty.html) to another file.
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-without-recording3.html");

        // same as before.
        runJetty();
    }

    @Test(threadPoolSize = 1, invocationCount = 1)
    public void runTwice() throws Exception {
        // run again to check time performance after classes are loaded.
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginBrowser.FEATURE_RECORDING, true);
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-one.html");
        runJetty();

        // run again to check time performance after classes are loaded.
        fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-two.html");
        runJetty();
    }

    public static void main(String[] args) throws Exception {
        TstngJetty tj = new TstngJetty();
        tj.prepareTest();
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginBrowser.FEATURE_RECORDING, false);
        fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, new File("src/test/resources/outcome/run"));
        for (int j = 0; j < 5; j++) {
            try {
                fh.put(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, "example-jetty-run" + j + ".html");
                tj.runJetty();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}