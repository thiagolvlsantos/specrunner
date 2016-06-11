package org.specrunner;

import org.joda.time.DateTime;
import org.junit.Before;
import org.specrunner.comparators.core.AbstractComparatorTime;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.Configuration;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.HtmlUnitDriverLocal;
import org.specrunner.webdriver.impl.WebDriverFactoryHtmlUnit;
import org.specrunner.webdriver.impl.htmlunit.WebConnectionFile;

public class AbstractTest {

    @Configuration
    public void onConfigure(IConfiguration cfg) {
        // cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryHtmlUnit.class.getName());

        cfg.add(PluginBrowser.FEATURE_REUSE, true);
        cfg.add(WebDriverFactoryHtmlUnit.FEATURE_REUSE, true);
        cfg.add(HtmlUnitDriverLocal.FEATURE_CONNECTION, WebConnectionFile.class.getName());
        cfg.add(WebConnectionFile.FEATURE_CLEAN, true);

        // cfg.add(PauseOnFailureNodeListener.FEATURE_PAUSE_ON_FAILURE, true);
        // cfg.add(PauseOnFailureNodeListener.FEATURE_SHOW_DIALOG, true);
    }

    @Before
    public void onBefore() {
        IExpressionFactory ef = SRServices.getExpressionFactory();

        // date expression elements
        ef.bindValue("pattern", "HH:mm:ss").bindClass("dt", DateTime.class);

        // longer tolerance
        SRServices.getFeatureManager().add(AbstractComparatorTime.FEATURE_TOLERANCE, 60000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }
}
