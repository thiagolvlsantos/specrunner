package org.specrunner.report.impl;

import java.util.Map;

import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterGroup;
import org.specrunner.result.IResultSet;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default reporter group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterGroupImpl extends CompositeImpl<IReporterGroup, IReporter> implements IReporterGroup {

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        for (IReporter r : getChildren()) {
            r.analyse(result, model);
        }
    }

    @Override
    public void resume() {
        for (IReporter r : getChildren()) {
            r.resume();
        }
    }

    @Override
    public void report() {
        for (IReporter r : getChildren()) {
            r.report();
        }
    }
}