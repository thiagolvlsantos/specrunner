package org.specrunner.plugins.core.var;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.UtilNode;

/**
 * Perform child actions before assertions.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginExecuteLatter extends PluginExecute {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    @DontEval
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        IPluginFactory factory = SRServices.get(IPluginFactory.class);
        String current = factory.getAlias(getClass());
        UtilNode.removeCss(node, current);
        String target = factory.getAlias(PluginExecute.class);
        UtilNode.appendCss(node, target);
        ((Element) node).addAttribute(new Attribute("onstart", "true"));
        UtilPlugin.performComandsFirst(context, result, node);
        return ENext.SKIP;
    }

}
