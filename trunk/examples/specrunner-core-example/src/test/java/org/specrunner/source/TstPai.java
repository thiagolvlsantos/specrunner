package org.specrunner.source;

import org.specrunner.junit.SpecRunnerJUnit;

public class TstPai {

    public TstPai() {
        super();
    }

    protected void runFile(String file) {
        SpecRunnerJUnit.defaultRun("src/test/resources/sources/" + file);
    }
}