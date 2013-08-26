package org.specrunner.download;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.junit.SRRunner;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;

@RunWith(SRRunner.class)
public class TestDownload {

    @BeforeClass
    public static void antes() {
        SpecRunnerServices.getFeatureManager().add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryChrome.class.getName());
    }
}
