package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Success: is not error, has importance 2.
 */
public class Success extends Status {

    /**
     * Singleton.
     */
    public static final Success INSTANCE = new Success();

    /**
     * Default constructor.
     */
    protected Success() {
        super("success", 2, false);
    }
}