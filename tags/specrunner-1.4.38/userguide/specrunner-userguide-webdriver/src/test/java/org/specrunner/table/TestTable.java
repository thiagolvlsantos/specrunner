package org.specrunner.table;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.AbstractTest;
import org.specrunner.SRServices;
import org.specrunner.junit.SRRunner;
import org.specrunner.webdriver.IWait;

@RunWith(SRRunner.class)
public class TestTable extends AbstractTest {

    @Before
    public void waitPrefix() {
        SRServices.getFeatureManager().add(IWait.FEATURE_WAITFOR_PREFIX, "//html");
        SRServices.getFeatureManager().add(IWait.FEATURE_WAITFOR_SUFFIX, "//body");
    }
}