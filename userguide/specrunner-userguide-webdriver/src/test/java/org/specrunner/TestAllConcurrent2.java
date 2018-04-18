package org.specrunner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.ConcurrentRunner;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 2)
public class TestAllConcurrent2 extends TestAllSequential {

    @BeforeClass
    public static void before() {
        SRServices.setThreadSafe(true);
    }

    @AfterClass
    public static void after() {
        SRServices.setThreadSafe(false);
    }
}
