package org.specrunner.plugins.impl.logical;

import java.util.Iterator;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.expressions.Unsilent;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
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
        IBlock block = context.peek();
        Object value = getValue();
        if (value == null || !(value instanceof Iterable)) {
            throw new PluginException("Value is null ou not found, or value has an invalid type (should be an Iterable).");
        }
        Iterator<?> ite = ((Iterable<?>) value).iterator();
        Nodes ns = node.query("descendant::tr");
        Element head = (Element) ns.get(0);
        Nodes hs = head.query("descendant::th");
        if (hs.size() == 0) {
            throw new PluginException("Missing header information.");
        }
        UtilNode.setIgnore(head);
        String pos = UtilEvaluator.asVariable("index");
        String item = UtilEvaluator.asVariable(var);
        int i = 1;
        boolean first = true;
        for (; i < ns.size(); i++) {
            Element row = (Element) ns.get(i);
            Nodes cs = row.query("descendant::td");
            if (hs.size() != cs.size()) {
                throw new PluginException("Number of headers (" + hs.size() + ") is different of columns (" + cs.size() + ").");
            }
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
                context.saveLocal(pos, String.valueOf(i - 1));
                if (ite.hasNext()) {
                    context.saveLocal(item, ite.next());
                    UtilPlugin.performChildren(row, context, result);
                } else {
                    if (first) {
                        result.addResult(Failure.INSTANCE, block, new PluginException("Returned enumeration missing itens."));
                        first = false;
                    }
                }
            } finally {
                context.clearLocal(item);
                context.clearLocal(pos);
                context.pop();
            }
        }
        Element ele = (Element) node;
        first = true;
        while (ite.hasNext()) {
            if (first) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Returned enumeration has more elements than expected."));
                first = false;
            }
            Element tr = new Element("tr");
            for (int j = 0; j < hs.size(); j++) {
                Element td = new Element("td");
                td.appendChild("${" + ((Element) hs.get(j)).getAttributeValue("value") + "}");
                tr.appendChild(td);
            }
            ele.appendChild(tr);
            try {
                context.push(context.newBlock(tr, this));
                context.saveLocal(pos, String.valueOf(i - 1));
                context.saveLocal(item, ite.next());
                UtilPlugin.performChildren(tr, context, result);
            } finally {
                context.clearLocal(item);
                context.clearLocal(pos);
                context.pop();
            }
            i++;
        }
        return ENext.SKIP;
    }
}
