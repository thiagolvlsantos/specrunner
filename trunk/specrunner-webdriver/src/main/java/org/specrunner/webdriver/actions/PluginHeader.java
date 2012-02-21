package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginBrowserAware;
import org.specrunner.webdriver.HtmlUnitDriverLocal;

public class PluginHeader extends AbstractPluginBrowserAware implements IAction {

    private String header;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        UtilLog.LOG.info("    On: " + getClass().getSimpleName() + " with " + client);
        if (client instanceof HtmlUnitDriverLocal) {
            Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
            if (header == null || tmp == null) {
                throw new PluginException("To set request header both, header and value, must be provided.");
            }
            String value = String.valueOf(tmp);
            ((HtmlUnitDriverLocal) client).setHeader(getHeader(), value);
            result.addResult(Status.SUCCESS, context.peek());
        } else {
            result.addResult(Status.WARNING, context.peek(), "Header setting is not supported by '" + (client != null ? client.getClass() : "null") + "'.");
        }
    }
}
