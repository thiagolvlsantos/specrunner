package org.specrunner.report;

/**
 * A reporter factory.
 * 
 * @author Thiago Santos
 * 
 */
public interface IReporterFactory {

    /**
     * Creates a reporter.
     * 
     * @return A reporter.
     */
    IReporter newReporter();
}