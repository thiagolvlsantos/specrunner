package org.specrunner.util.mapping;

import java.io.Serializable;

/**
 * Something resetable.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResetable extends Serializable {

    /**
     * Initialize the converter.
     */
    void initialize();
}
