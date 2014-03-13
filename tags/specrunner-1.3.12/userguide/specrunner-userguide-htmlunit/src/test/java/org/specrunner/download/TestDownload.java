package org.specrunner.download;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

import com.gargoylesoftware.htmlunit.WebClient;

@RunWith(SRRunner.class)
public class TestDownload {

    @BeforeClass
    public static void antes() {
        // SRServices.getFeatureManager().add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
    }

    public void qualBrowser(WebClient client) {
        System.out.println("CLIENT:" + client);
    }

    public void numero(int value) {
        System.out.println("VALUE:" + value);
    }
}
