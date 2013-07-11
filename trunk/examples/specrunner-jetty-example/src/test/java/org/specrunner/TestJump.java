package org.specrunner;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.jetty.JettyStringProvider;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.runner.impl.FilterDefault;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.assertions.PluginCompareDate;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;

public class TestJump {

    @Before
    public void prepareTest() throws SpecRunnerException {
        IExpressionFactory ef = SpecRunnerServices.get(IExpressionFactory.class);
        // add predefined objRects that can be used in expressions
        ef.bindValue("pattern", "HH:mm:ss");
        // add predefined classes that can be used in expressions, default
        // constructor is invoked.
        ef.bindClass("dt", DateTime.class);

        // longer tolerance
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(PluginCompareDate.FEATURE_TOLERANCE, 60000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }

    @Test
    public void example() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html");
    }

    @Test
    public void exampleChrome() {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(FilterDefault.FEATURE_DISABLED_ALIASES, Arrays.asList("pause"));
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryChrome.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", cfg);
    }

    @Test
    public void exampleLocal() {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(FilterDefault.FEATURE_DISABLED_ALIASES, Arrays.asList("jettyStart", "pause"));
        cfg.add(JettyStringProvider.FEATURE_URL, "http://localhost:8080");
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jettyLocal.html", cfg);
    }

    @Test
    public void exampleGlobal() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        fm.add(FilterDefault.FEATURE_DISABLED_ALIASES, Arrays.asList("jettyStart", "pause"));
        fm.add(JettyStringProvider.FEATURE_URL, "http://localhost:8080");
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jettyGlobal.html");
    }
}