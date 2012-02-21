package org.specrunner;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestStyles {

    @Test
    public void styleNatural1() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/style_natural1.html");
    }

    @Test
    public void style() {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/styles.html");
    }

}
