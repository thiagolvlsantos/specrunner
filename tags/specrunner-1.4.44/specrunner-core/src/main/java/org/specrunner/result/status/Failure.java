package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Failure: is error, has importance 3.
 */
public class Failure extends Status {

    /**
     * Singleton.
     */
    public static final Failure INSTANCE = new Failure();

    /**
     * Default constructor.
     */
    protected Failure() {
        // CHECKSTYLE:OFF
        super("failure", 3, true);
        // CHECKSTYLE:ON
    }
}
