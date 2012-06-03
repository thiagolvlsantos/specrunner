package org.specrunner.plugins.type;

import org.specrunner.plugins.ActionType;

/**
 * Assertion: is not error, has importance 2.
 */
public class Assertion extends ActionType {

    /**
     * Singleton.
     */
    public static final Assertion INSTANCE = new Assertion();

    /**
     * Default constructor.
     */
    protected Assertion() {
        super("assertion", 2);
    }
}