package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Warning: is not error, has importance 1.
 */
public class Warning extends Status {

    /**
     * Singleton.
     */
    public static final Warning INSTANCE = new Warning();

    /**
     * Default constructor.
     */
    protected Warning() {
        super("warning", false, 1);
    }
}