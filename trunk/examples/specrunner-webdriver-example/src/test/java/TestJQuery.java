import java.io.File;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

public class TestJQuery {
    private static final String DRAGDROP = "src/test/resources/income/dragdrop.html";

    @Test
    public void rodarDragDropChrome() throws Exception {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe");
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, ChromeDriver.class.getName());
        SpecRunnerJUnit.defaultRun(DRAGDROP, cfg);
    }

    @Test
    public void rodarDragDropChrome2() throws Exception {
        ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
        ChromeDriverService service = builder.usingDriverExecutable(new File("C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe")).usingPort(9090).build();
        service.start();
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_INSTANCE, new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome()));
        SpecRunnerJUnit.defaultRun(DRAGDROP, cfg);
        service.stop();
    }

    @Test
    public void rodarDragDropFireFox() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_TYPE, FirefoxDriver.class.getName());
        SpecRunnerJUnit.defaultRun(DRAGDROP, cfg);
    }

    @Test
    public void rodarDragDropIe() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
        SpecRunnerJUnit.defaultRun(DRAGDROP, cfg);
    }

    @Test
    public void testGoogle() throws Exception {
        WebDriver driver = new ChromeDriver();
        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));
        // Enter something to search for
        element.sendKeys("Cheese!");
        // Now submit the form. WebDriver will find the form for us from the
        // element
        element.submit();
        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }

    @Test
    public void testBusca() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/busca.html", cfg);
    }

    @Test
    public void testMobile() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginBrowser.FEATURE_RECORDING, false);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/mobile.html", cfg);
    }
}
