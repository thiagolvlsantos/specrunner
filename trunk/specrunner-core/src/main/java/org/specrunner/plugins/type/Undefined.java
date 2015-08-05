package org.specrunner.plugins.type;

import org.specrunner.plugins.ActionType;

/**
 * Undefined: has importance -1.
 */
public class Undefined extends ActionType {

    /**
     * Singleton.
     */
    public static final Undefined INSTANCE = new Undefined();

    /**
     * Default constructor.
     */
    protected Undefined() {
        super("undef", -1);
    }
}
