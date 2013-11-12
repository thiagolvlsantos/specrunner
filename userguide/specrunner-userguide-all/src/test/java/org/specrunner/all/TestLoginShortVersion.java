package org.specrunner.all;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.jetty.JettyStringProvider;
import org.specrunner.jetty.PluginStartJetty;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.actions.PluginOpen;
import org.specrunner.webdriver.actions.PluginStartIn;

@RunWith(SRRunner.class)
public class TestLoginShortVersion {

    @Configuration
    public void cfg(IConfiguration cfg) {
        // cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
    }

    @Before
    public void before() throws PluginException {
        IPluginFactory pf = SRServices.get(IPluginFactory.class);
        IPluginGroup group = new PluginGroupImpl();
        // jetty
        PluginStartJetty jetty = new PluginStartJetty();
        jetty.setFile("/jetty.xml");
        group.add(jetty);
        // browser
        group.add(new PluginBrowser());
        // start page
        PluginStartIn start = new PluginStartIn();
        start.setProvider(JettyStringProvider.class.getName());
        group.add(start);
        // open page
        PluginOpen open = new PluginOpen();
        open.setUrl("/application");
        group.add(open);
        // by alias
        pf.bind(PluginKind.CSS, "start", group);
        pf.bind(PluginKind.ELEMENT, "start", group);
    }
}