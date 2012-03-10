import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.configuration.impl.ConfigurationFactoryImpl;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 6)
public class TestReuse {

    protected String getNome() {
        return "src/test/resources/income/browser_reuse.html";
    }

    @BeforeClass
    public static void prepareResources() {
        SpecRunnerServices.get().bind(IConfigurationFactory.class, new ConfigurationFactoryImpl() {
            @Override
            public IConfiguration newConfiguration() {
                IConfiguration cfg = super.newConfiguration();
                cfg.add(PluginBrowser.FEATURE_RECORDING, Boolean.FALSE);
                cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
                return cfg;
            }
        });
    }

    @Test
    public void run1() throws Exception {
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_reuse1.html");
    }

    @Test
    public void run2() throws Exception {
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_reuse2.html");
    }

    @Test
    public void run3() throws Exception {
        SpecRunnerJUnit.defaultRun(getNome(), "src/test/resources/outcome/browser_reuse3.html");
    }
}