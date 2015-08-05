package org.specrunner.download;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestDownload {

    @BeforeClass
    public static void antes() {
        // SRServices.getFeatureManager().add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
    }

    public void qualBrowser(WebDriver driver) {
        System.out.println("DRIVER:" + driver);
    }

    public void numero(int value) {
        System.out.println("VALUE:" + value);
    }
}
