package org.specrunner.source;

import nu.xom.Element;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;

public class PluginSysout extends AbstractPlugin {

    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        return ENext.DEEP;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (prefix != null) {
            Element p = new Element("span");
            Text t = new Text(prefix);
            p.appendChild(t);
            result.addResult(Success.INSTANCE, context.newBlock(p, this));
            ((Element) context.getNode()).insertChild(p, 0);
        }
        if (suffix != null) {
            ((Element) context.getNode()).insertChild(suffix, context.getNode().getChildCount());
        }
    }
}
