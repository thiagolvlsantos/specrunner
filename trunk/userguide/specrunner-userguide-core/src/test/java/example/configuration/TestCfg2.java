package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.junit.ExpectedMessage;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg2 {

    @ExpectedMessage("Expected result of 'public boolean example.configuration.TestCfg2.checkCfg(java.lang.String)' must be 'true'. Received 'false'.")
    public boolean checkCfg(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }

    public boolean checkAll(String value) {
        return value.equals(SpecRunnerServices.getFeatureManager().get(PluginBean.FEATURE_WAIT));
    }
}