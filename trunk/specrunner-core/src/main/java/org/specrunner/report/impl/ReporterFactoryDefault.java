package org.specrunner.report.impl;

/**
 * Default factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterFactoryDefault extends ReporterFactoryImpl {

    /**
     * Default constructor.
     */
    public ReporterFactoryDefault() {
        super(new ReporterGroupImpl().add(new ReporterTxt()));
    }
}