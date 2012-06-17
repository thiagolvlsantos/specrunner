package org.specrunner.report.impl;

import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;

/**
 * Default factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterFactoryImpl implements IReporterFactory {

    /**
     * Reused reporter.
     */
    protected IReporter reporter;

    /**
     * Creates a new reporter factory.
     * 
     * @param reporter
     *            The reporter.
     */
    public ReporterFactoryImpl(IReporter reporter) {
        setReporter(reporter);
    }

    /**
     * Gets the reporter.
     * 
     * @return The reporter.
     */
    public IReporter getReporter() {
        return reporter;
    }

    /**
     * Set the reporter.
     * 
     * @param reporter
     *            The reporter.
     */
    public void setReporter(IReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public IReporter newReporter() {
        return reporter;
    }
}