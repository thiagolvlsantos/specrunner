package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg1 {

    @Configuration
    public void prepare(IConfiguration cfg) {
        cfg.add(PluginBean.BEAN_NAME, "any");
        SRServices.getFeatureManager().add(PluginBean.FEATURE_WAIT, "wait");
    }

    public boolean checkCfg(String value) {
        return value.equals(SRServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }

    public boolean checkAll(String value) {
        return value.equals(SRServices.getFeatureManager().get(PluginBean.FEATURE_WAIT));
    }
}