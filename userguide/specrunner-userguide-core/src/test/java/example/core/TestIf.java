package example.core;

import org.junit.Test;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.core.flow.PluginIfBranch;

//CHECKSTYLE:OFF
public class TestIf {

    @Test
    public void if1() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/if.html");
    }

    @Test
    public void if2() {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginIfBranch.FEATURE_HIDE, Boolean.FALSE);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/if.html", "src/test/resources/outcome/ifHide.html", cfg);
    }
}
// CHECKSTYLE:ON
