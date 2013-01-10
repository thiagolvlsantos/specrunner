package org.specrunner.result.status;

import org.specrunner.result.Status;

/**
 * Detail: is not error, has importance -1.
 */
public class Detail extends Status {

    /**
     * Singleton.
     */
    public static final Detail INSTANCE = new Detail();

    /**
     * Default constructor.
     */
    protected Detail() {
        // CHECKSTYLE:OFF
        super("detail", -2, false);
        // CHECKSTYLE:ON
    }
}