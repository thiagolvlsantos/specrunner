package org.specrunner.download;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestDownload {

    @BeforeClass
    public static void antes() {
        // SpecRunnerServices.getFeatureManager().add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
    }
}
