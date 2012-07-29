package org.specrunner.htmlunit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log LOG = LogFactory.getLog(DefaultCssErrorHandler.class);

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
        if (LOG.isFatalEnabled()) {
            super.fatalError(exception);
        }
    }

}
