package org.specrunner.report;

import org.specrunner.util.composite.IComposite;

/**
 * A reporter group.
 * 
 * @author Thiago Santos
 * 
 */
public interface IReporterGroup extends IReporter, IComposite<IReporterGroup, IReporter> {
}