package org.specrunner.report.impl;

import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;
import org.specrunner.report.IReporterFactoryGroup;
import org.specrunner.report.IReporterGroup;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default reporter factory group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterFactoryGroupImpl extends CompositeImpl<IReporterFactoryGroup, IReporterFactory> implements IReporterFactory {

    @Override
    public IReporter newReporter() {
        IReporterGroup result = new ReporterGroupImpl();
        for (IReporterFactory irf : getChildren()) {
            result.add(irf.newReporter());
        }
        return result;
    }

}