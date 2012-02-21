package org.specrunner;

import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

public class TestGoogle {

    @AfterClass
    public static void release() {
        SpecRunnerServices.release();
    }

    @Test
    public void runGoogle() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/example-google.html");
    }

    @Test
    public void bddGoogle() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/bdd-google.html", cfg);
    }

    @Test
    public void bddGoogleChrome() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe");
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, ChromeDriver.class.getName());
        SpecRunnerJUnit.defaultRun("src/test/resources/income/bdd-google.html", cfg);
    }
}