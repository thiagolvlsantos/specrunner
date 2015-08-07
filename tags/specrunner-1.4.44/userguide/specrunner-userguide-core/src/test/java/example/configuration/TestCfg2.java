package example.configuration;

import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.ExpectedMessage;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.var.PluginBean;

@RunWith(SRRunner.class)
public class TestCfg2 extends TestCfg1 {

    @Override
    @Configuration
    public void prepare(IConfiguration cfg) {
        // override parent to not add BEAN_NAME
        SRServices.getFeatureManager().add(PluginBean.FEATURE_WAIT, "wait");
    }

    @Override
    @ExpectedMessage("Expected result of 'public boolean example.configuration.TestCfg2.checkCfg(java.lang.String)' must be 'true'. Received 'false'.")
    public boolean checkCfg(String value) {
        // in this example BEAN_NAME is not bound
        return value.equals(SRServices.getFeatureManager().get(PluginBean.BEAN_NAME));
    }
}
