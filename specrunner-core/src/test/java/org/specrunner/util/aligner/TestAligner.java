package org.specrunner.util.aligner;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.ConcurrentRunner;

/**
 * Testing.
 * 
 * @author Thiago Santos
 * 
 */
// CHECKSTYLE:OFF
@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 2)
public class TestAligner {

    @BeforeClass
    public static void before() {
        SRServices.setThreadSafe(true);
    }

    @AfterClass
    public static void after() {
        SRServices.setThreadSafe(false);
    }

    @Before
    public void antes() {
        SRServices.getExpressionFactory().bindValue("alignerFactory", SRServices.get(IStringAlignerFactory.class));
    }

    @Test
    public void test() {
        SpecRunnerJUnit.defaultRun("src/test/income/util/aligner/align.html");
    }
}
// CHECKSTYLE:ON
