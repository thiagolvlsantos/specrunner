package org.specrunner.webdriver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl;

/**
 * Default implementation of incorrectness listener which log only if required.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class OptimizedIncorrectnessListener extends IncorrectnessListenerImpl {

    /**
     * Logger, same of IncorrectnessListenerImpl.
     */
    private static final Log LOG = LogFactory.getLog(IncorrectnessListenerImpl.class);

    @Override
    public void notify(String message, Object origin) {
        if (LOG.isWarnEnabled()) {
            super.notify(message, origin);
        }
    }

}
