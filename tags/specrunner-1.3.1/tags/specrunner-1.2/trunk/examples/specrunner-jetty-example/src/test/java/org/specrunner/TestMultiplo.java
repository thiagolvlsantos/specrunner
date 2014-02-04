package org.specrunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.assertions.PluginCompareDate;
import org.specrunner.webdriver.impl.FinderXPath;

@RunWith(Parameterized.class)
public class TestMultiplo {

    private final String numero;

    public TestMultiplo(String numero) {
        this.numero = numero;
    }

    @Parameters
    public static Collection<Object[]> getNumeros() {
        List<Object[]> i = new LinkedList<Object[]>();
        for (int j = 0; j < 10; j++) {
            i.add(new Object[] { "numero" + j });
        }
        return i;
    }

    @Before
    public void prepareTest() throws SpecRunnerException {
        IExpressionFactory ef = SpecRunnerServices.get(IExpressionFactory.class);
        // add predefined objects that can be used in expressions
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
    public void runJettyChrome() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        // cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-jetty.html", "src/test/resources/outcome/example-jetty-" + numero + ".html", cfg);
    }
}