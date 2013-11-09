package org.specrunner;

import org.joda.time.DateTime;
import org.junit.Before;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.Configuration;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.assertions.PluginCompareDate;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;

public class AbstractTest {

    @Configuration
    public void onConfigure(IConfiguration cfg) {
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryChrome.class.getName());
    }

    @Before
    public void onBefore() {
        IExpressionFactory ef = SpecRunnerServices.get(IExpressionFactory.class);

        // date expression elements
        ef.bindValue("pattern", "HH:mm:ss").bindClass("dt", DateTime.class);

        // longer tolerance
        SpecRunnerServices.getFeatureManager().add(PluginCompareDate.FEATURE_TOLERANCE, 60000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }
}