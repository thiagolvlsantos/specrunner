package org.specrunner.source;

import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.result.IResultSet;

public class PluginEcho extends AbstractPlugin {

    private int times = 1;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
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
            UtilPlugin.setIgnore(copy);
            UtilPlugin.performChildren(copy, context, result);
        }
        return ENext.SKIP;
    }
}
