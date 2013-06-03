package org.specrunner.plugins.impl.logical;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.expressions.Unsilent;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.xom.UtilNode;

/**
 * Perform table verifications.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginVerifyRows extends AbstractPluginValue {

    /**
     * Iterator variable name.
     */
    private String var = "item";

    /**
     * The object variable name.
     * 
     * @return The variable name.
     */
    public String getVar() {
        return var;
    }

    /**
     * Set the variable name.
     * 
     * @param var
     *            The name.
     */
    public void setVar(String var) {
        this.var = var;
    }

    @Override
    @Unsilent
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        Object value = getValue();
        if (value == null || !(value instanceof List)) {
            throw new PluginException("Value not found or invalid type (should be List).");
        }
        List<?> l = (List<?>) value;
        Nodes ns = node.query("descendant::tr");
        Element head = (Element) ns.get(0);
        Nodes hs = head.query("descendant::th");
        UtilNode.setIgnore(head);
        String pos = UtilEvaluator.asVariable("index");
        String item = UtilEvaluator.asVariable(var);
        for (int i = 1; i < ns.size(); i++) {
            Element row = (Element) ns.get(i);
            Nodes cs = row.query("descendant::td");
            for (int j = 0; j < cs.size(); j++) {
                Element h = (Element) hs.get(j);
                Element c = (Element) cs.get(j);
                for (int k = 0; k < h.getAttributeCount(); k++) {
                    Attribute att = h.getAttribute(k);
                    c.addAttribute((Attribute) att.copy());
                }
            }
            context.push(context.newBlock(row, this));
            try {
                context.saveLocal(pos, "" + (i - 1));
                context.saveLocal(item, l.get(i - 1));
                UtilPlugin.performChildren(row, context, result);
            } finally {
                context.clearLocal(pos);
                context.clearLocal(item);
                context.pop();
            }
        }
        return ENext.SKIP;
    }
}
