package org.specrunner.webdriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.CSSParseException;

import com.gargoylesoftware.htmlunit.DefaultCssErrorHandler;

/**
 * Default implementation of CSS handler which log only if required.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class OptimizedCssErrorHandler extends DefaultCssErrorHandler {

    /**
     * Logger, same of DefaultCssErrorHandler.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCssErrorHandler.class.getName());

    @Override
    public void warning(CSSParseException exception) {
        if (LOG.isWarnEnabled()) {
            super.warning(exception);
        }
    }

    @Override
    public void error(CSSParseException exception) {
        if (LOG.isErrorEnabled()) {
            super.error(exception);
        }
    }

    @Override
    public void fatalError(CSSParseException exception) {
        if (LOG.isErrorEnabled()) {
            super.error(exception);
        }
    }

}
