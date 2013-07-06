package org.specrunner.plugins.impl.flow;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.runner.RunnerException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.xom.UtilNode;

/**
 * Perform table actions.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginExecuteRows extends AbstractPluginScoped {

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
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        if (!(node instanceof Element)) {
            throw new PluginException("Annotation only applicable to table elements.");
        }
        Element table = (Element) node;
        if (!"table".equalsIgnoreCase(table.getLocalName())) {
            throw new PluginException("The element is not a table.");
        }
        Nodes ns = node.query("descendant::tr");
        if (ns.size() == 0) {
            throw new PluginException("Missing rows.");
        }
        Element head = (Element) ns.get(0);
        Nodes hs = head.query("descendant::th");
        if (hs.size() == 0) {
            throw new PluginException("Missing header information.");
        }
        UtilNode.setIgnore(head);
        String pos = UtilEvaluator.asVariable("index");
        int i = 1;
        for (; i < ns.size(); i++) {
            Element row = (Element) ns.get(i);
            for (int k = 0; k < table.getAttributeCount(); k++) {
                Attribute att = (Attribute) table.getAttribute(k).copy();
                if (att.getLocalName().equals("value")) {
                    att.setLocalName("value.");
                }
                row.addAttribute(att);
            }
            row.addAttribute(new Attribute("class", "execute"));

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
                context.getRunner().run(row, context, result);
            } catch (RunnerException e) {
                e.printStackTrace();
            } finally {
                context.clearLocal(pos);
                context.pop();
            }
        }
        return ENext.SKIP;
    }
}
