package org.specrunner;

import org.junit.Test;
import org.specrunner.context.core.LazyExpressionModel;
import org.specrunner.jetty.JettyStringProvider;
import org.specrunner.jetty.PluginStartJetty;
import org.specrunner.junit.SpecRunnerJUnitPlugin;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.actions.PluginOpen;
import org.specrunner.webdriver.actions.PluginStartIn;
import org.specrunner.webdriver.actions.window.PluginSize;
import org.specrunner.webdriver.assertions.PluginCompareText;
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
        present.getParameters().setParameter("by", "xpath://title", null);
        group.add(present);

        PluginPresent presentOne = new PluginPresent();
        presentOne.setCount(3);
        presentOne.getParameters().setParameter("by", "xpath://title", null);
        group.add(presentOne);

        PluginCompareText compare = new PluginCompareText();
        compare.setValue("SWicket Quickstart Archetype Homepage");
        compare.getParameters().setParameter("by", "xpath://title", null);
        group.add(compare);

        SpecRunnerJUnitPlugin.defaultRun(group);
    }
}