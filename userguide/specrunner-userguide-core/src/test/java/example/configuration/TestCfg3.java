package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.annotations.Configuration;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg3 extends TestCfg2 {

    @Configuration
    public void prepareOther(IConfiguration cfg) {
        // complement super class configuration (order is not deterministic).
        cfg.add(PluginBean.BEAN_NAME, "any");
    }

    @Override
    public boolean checkCfg(String value) {
        return value.equals(SRServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }
}
