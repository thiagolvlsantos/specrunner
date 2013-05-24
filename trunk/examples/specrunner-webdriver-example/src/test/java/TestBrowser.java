import org.junit.Test;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

public class TestBrowser {

    @Test
    public void rodarBrowserHtmlUnit() throws Exception {
        SpecRunnerJUnit.defaultRun(getNome());
    }

    protected String getNome() {
        return "src/test/resources/income/browser.html";
    }

    @Test
    public void rodarBrowserFirefox() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, FirefoxDriver.class.getName());
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_fire.html", cfg);
    }

    @Test
    public void rodarBrowserAndroid() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, AndroidDriver.class.getName());
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_android.html", cfg);
    }

    @Test
    public void rodarBrowserIe() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_ie.html", cfg);
    }

    @Test
    public void rodarBrowserFactory() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/factory.html");
    }
}