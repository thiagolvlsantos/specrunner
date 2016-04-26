package org.specrunner;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 2)
public class TestAllConcurrent2 extends TestAllSequential {

    @BeforeClass
    public static void set() {
        System.setProperty(SRServices.SR_THREAD_SAFE, String.valueOf(true));
    }
}
