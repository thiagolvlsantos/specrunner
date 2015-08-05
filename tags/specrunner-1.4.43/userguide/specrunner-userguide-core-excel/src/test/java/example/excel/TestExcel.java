package example.excel;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.include.PluginInclude;

@RunWith(SRRunner.class)
public class TestExcel {

    @BeforeClass
    public static void before() {
        SRServices.getFeatureManager().add(PluginInclude.FEATURE_EXPANDED, true);
    }
}
