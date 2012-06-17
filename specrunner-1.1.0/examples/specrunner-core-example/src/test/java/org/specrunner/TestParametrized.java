package org.specrunner;

import java.io.File;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.specrunner.junit.SpecRunnerParametrized;

public class TestParametrized extends SpecRunnerParametrized {

    public TestParametrized(Entry entry) {
        super(entry);
    }

    // JUnit annotation
    @SuppressWarnings("unchecked")
    @Parameters
    public static Collection<Object[]> getTestFiles() {
        // input directory
        File inDir = new File("src/test/resources/sources");
        // output directory
        File outDir = new File("src/test/resources/out");
        // ending with 'e.html' OR starting with 'cust' OR containing 'st'
        return merge(endsWith(inDir, "e.html", outDir), startsWith(inDir, "cust", outDir), contains(inDir, "st", outDir));
    }
}