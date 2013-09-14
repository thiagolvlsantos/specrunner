package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg1 {

    @Configuration
    public void prepare(IConfiguration cfg) {
        cfg.add(PluginBean.BEAN_NAME, "any");
        SpecRunnerServices.getFeatureManager().add(PluginBean.FEATURE_WAIT, "wait");
    }

    public boolean checkCfg(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }

    public boolean checkAll(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.FEATURE_WAIT));
    }
}