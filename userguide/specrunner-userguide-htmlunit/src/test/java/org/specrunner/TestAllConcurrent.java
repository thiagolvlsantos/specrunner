package org.specrunner;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.junit.ConcurrentRunner;

@RunWith(ConcurrentRunner.class)
public class TestAllConcurrent extends TestAllSequential {

    @BeforeClass
    public static void set() {
        SRServices.setThreadSafe(true);
    }
}
