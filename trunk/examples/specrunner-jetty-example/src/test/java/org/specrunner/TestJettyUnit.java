package org.specrunner;

import org.junit.AfterClass;
import org.junit.Test;
import org.specrunner.context.impl.LazyExpressionModel;
import org.specrunner.jetty.JettyStringProvider;
import org.specrunner.jetty.PluginStartJetty;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.impl.PluginGroupImpl;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.actions.PluginStartIn;
import org.specrunner.webdriver.actions.PluginOpen;
import org.specrunner.webdriver.actions.window.PluginSize;
import org.specrunner.webdriver.assertions.PluginCompare;
import org.specrunner.webdriver.assertions.PluginContains;
import org.specrunner.webdriver.assertions.PluginNotContains;
import org.specrunner.webdriver.assertions.PluginPresent;

public class TestJettyUnit {

    @Test
    public void play() throws Exception {
        IPluginGroup group = new PluginGroupImpl();

        PluginStartJetty jetty = new PluginStartJetty();
        jetty.setFile("/jetty.xml");
        jetty.setDynamic(true);
        jetty.setReuse(true);
        group.add(jetty);

        PluginBrowser browser = new PluginBrowser();
        browser.setReuse(true);
        // browser.setWebdriverfactory(WebDriverFactoryIe.class.getName());
        group.add(browser);

        PluginSize size = new PluginSize();
        size.setConditionModel(new LazyExpressionModel<Boolean>("!browser_type.equals(\"HtmlUnitDriverLocal\")"));
        size.setWidth(500);
        size.setHeight(500);
        group.add(size);

        PluginStartIn in = new PluginStartIn();
        in.setProvider(JettyStringProvider.class.getName());
        group.add(in);

        PluginOpen open = new PluginOpen();
        open.setUrl("/application");
        group.add(open);

        PluginContains contains = new PluginContains();
        contains.setValue("Quickstart");
        group.add(contains);

        PluginNotContains notContains = new PluginNotContains();
        notContains.setValue("Blueprint");
        group.add(notContains);

        PluginPresent present = new PluginPresent();
        present.setParameter("by", "xpath://title");
        group.add(present);

        PluginPresent presentOne = new PluginPresent();
        presentOne.setCount(3);
        presentOne.setParameter("by", "xpath://title");
        group.add(presentOne);

        PluginCompare compare = new PluginCompare();
        compare.setValue("SWicket Quickstart Archetype Homepage");
        compare.setParameter("by", "xpath://title");
        group.add(compare);

        SpecRunnerJUnit.defaultRun(group);
    }

    @AfterClass
    public static void release() {
        SpecRunnerServices.release();
    }
}