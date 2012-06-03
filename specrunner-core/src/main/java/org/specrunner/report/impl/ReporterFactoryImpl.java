package org.specrunner.report.impl;

import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;

public class ReporterFactoryImpl implements IReporterFactory {

    protected IReporter reporter;

    public ReporterFactoryImpl(IReporter reporter) {
        setReporter(reporter);
    }

    public ReporterFactoryImpl() {
        super();
    }

    public IReporter getReporter() {
        return reporter;
    }

    public void setReporter(IReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public IReporter newReporter() {
        return reporter;
    }
}