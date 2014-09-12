package org.specrunner.plugins.type;

import org.specrunner.plugins.ActionType;

/**
 * Control: has importance 0.
 */
public class Control extends ActionType {

    /**
     * Singleton.
     */
    public static final Control INSTANCE = new Control();

    /**
     * Default constructor.
     */
    protected Control() {
        super("control", 0);
    }
}