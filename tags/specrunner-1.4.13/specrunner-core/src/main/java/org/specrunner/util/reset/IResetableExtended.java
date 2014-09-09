package org.specrunner.util.reset;

import java.io.Serializable;

import org.specrunner.context.IContext;

/**
 * Something resetable with context.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResetableExtended extends Serializable {

    /**
     * Initialize something.
     * 
     * @param context
     *            A context.
     */
    void initialize(IContext context);
}
