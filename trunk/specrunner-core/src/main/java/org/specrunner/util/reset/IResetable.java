package org.specrunner.util.reset;

import java.io.Serializable;

/**
 * Something resetable.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResetable extends Serializable {

    /**
     * Initialize something.
     */
    void initialize();
}
