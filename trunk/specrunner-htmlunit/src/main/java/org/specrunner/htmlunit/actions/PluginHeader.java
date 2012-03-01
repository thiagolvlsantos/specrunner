package org.specrunner.htmlunit.actions;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginBrowserAware;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebClient;

public class PluginHeader extends AbstractPluginBrowserAware implements IAction {

    private String header;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException {
        UtilLog.LOG.info("    On: " + getClass().getSimpleName() + " with " + client);
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        if (header == null || tmp == null) {
            throw new PluginException("To set request header both, header and value, must be provided.");
        }
        String value = String.valueOf(tmp);
        client.addRequestHeader(getHeader(), value);
        result.addResult(Status.SUCCESS, context.peek());
    }
}
