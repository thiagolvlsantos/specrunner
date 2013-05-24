package org.specrunner.source;

import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestMap {

    protected void runFile(String file) {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/" + file);
    }

    @Test
    public void exemplo() {
        runFile("map.html");
    }
}
