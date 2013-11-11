package org.specrunner.source;

import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.UtilNode;

public class PluginEcho extends AbstractPlugin {

    private int times = 1;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        node.detach();
        for (int i = 0; i < times; i++) {
            Node copy = node.copy();
            parent.insertChild(copy, index++);
            UtilNode.setIgnore(copy);
            UtilPlugin.performChildren(copy, context, result);
        }
        return ENext.SKIP;
    }
}
