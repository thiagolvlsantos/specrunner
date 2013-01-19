package org.specrunner.util.aligner;

import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.SpecRunnerJUnit;

/**
 * Testing.
 * 
 * @author Thiago Santos
 * 
 */
// CHECKSTYLE:OFF
public class TestAligner {

    @Test
    public void test() {
        SpecRunnerServices.get(IExpressionFactory.class).bindPredefinedValue("alignerFactory", SpecRunnerServices.get(IStringAlignerFactory.class));
        SpecRunnerJUnit.defaultRun("src/test/income/util/aligner/align.html");
    }
}
// CHECKSTYLE:ON