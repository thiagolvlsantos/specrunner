package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.ExpectedMessage;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg2 extends TestCfg1 {

    @Override
    @Configuration
    public void prepare(IConfiguration cfg) {
        SpecRunnerServices.getFeatureManager().add(PluginBean.FEATURE_WAIT, "wait");
    }

    @Override
    @ExpectedMessage("Expected result of 'public boolean example.configuration.TestCfg2.checkCfg(java.lang.String)' must be 'true'. Received 'false'.")
    public boolean checkCfg(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }
}