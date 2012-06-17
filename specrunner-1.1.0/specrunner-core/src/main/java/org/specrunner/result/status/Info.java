package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Info: is not error, has importance 0.
 */
public class Info extends Status {

    /**
     * Singleton.
     */
    public static final Info INSTANCE = new Info();

    /**
     * Default constructor.
     */
    protected Info() {
        super("info", false, 0);
    }
}