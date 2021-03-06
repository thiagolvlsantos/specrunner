package org.specrunner.util.aligner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.ConcurrentSuite;

/**
 * Testing.
 * 
 * @author Thiago Santos
 * 
 */
// CHECKSTYLE:OFF
@RunWith(ConcurrentSuite.class)
@SuiteClasses(TestAligner.class)
@Concurrent(threads = 2)
public class TestAlignerSuite {
}
// CHECKSTYLE:ON
