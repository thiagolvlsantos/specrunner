package org.specrunner.util.aligner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;

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

    @Before
    public void antes() {
        SpecRunnerServices.get(IExpressionFactory.class).bindValue("alignerFactory", SpecRunnerServices.get(IStringAlignerFactory.class));
    }

    @Test
    public void test() {
        SpecRunnerJUnit.defaultRun("src/test/income/util/aligner/align.html");
    }
}
// CHECKSTYLE:ON