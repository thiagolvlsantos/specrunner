package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg3 extends TestCfg2 {

    @Configuration
    public void prepareOther(IConfiguration cfg) {
        // complement super class configuration (order is not deterministic).
        cfg.add(PluginBean.BEAN_NAME, "any");
    }

    @Override
    public boolean checkCfg(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }
}