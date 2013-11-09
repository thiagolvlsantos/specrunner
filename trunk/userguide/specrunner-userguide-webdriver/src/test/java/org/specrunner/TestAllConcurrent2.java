package org.specrunner;

import org.junit.runner.RunWith;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 2)
public class TestAllConcurrent2 extends TestAllSequential {
}