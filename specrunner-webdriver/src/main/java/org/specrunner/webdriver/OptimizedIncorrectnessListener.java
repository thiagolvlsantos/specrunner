package org.specrunner.webdriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(IncorrectnessListenerImpl.class.getName());

    @Override
    public void notify(String message, Object origin) {
        if (LOG.isWarnEnabled()) {
            super.notify(message, origin);
        }
    }

}
