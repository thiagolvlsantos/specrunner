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
        super("detail", false, -1);
    }
}