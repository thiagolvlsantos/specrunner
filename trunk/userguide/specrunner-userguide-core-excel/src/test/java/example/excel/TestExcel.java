package example.excel;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.include.PluginInclude;

@RunWith(SRRunner.class)
public class TestExcel {

    @BeforeClass
    public static void before() {
        SpecRunnerServices.getFeatureManager().add(PluginInclude.FEATURE_EXPANDED, true);
    }
}