package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Ignored: is not error, has importance -1.
 */
public class Ignored extends Status {

    /**
     * Singleton.
     */
    public static final Ignored INSTANCE = new Ignored();

    /**
     * Default constructor.
     */
    protected Ignored() {
        super("ignored", -1, false);
    }
}
