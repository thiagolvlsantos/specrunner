package org.specrunner.report;

import org.specrunner.util.composite.IComposite;

/**
 * A reporter factory group.
 * 
 * @author Thiago Santos
 * 
 */
public interface IReporterFactoryGroup extends IReporterFactory, IComposite<IReporterFactoryGroup, IReporterFactory> {
}