package org.specrunner.plugins.core.logical;

import java.util.Iterator;

import org.specrunner.SRServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.expressions.Unsilent;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.UnstackedPluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Perform table verifications.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginVerifyRows extends AbstractPluginValue {

    /**
     * Default constructor.
     */
    public PluginVerifyRows() {
        setName("item");
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
        Object value = getValue();
        if (value == null) {
            throw new UnstackedPluginException("Value is null or not found.");
        }
        if (!(value instanceof Iterable)) {
            throw new UnstackedPluginException("Value has an invalid type (should be an Iterable). Current type is '" + (value != null ? value.getClass().getName() : "null") + "'.");
        }
        Iterator<?> ite = ((Iterable<?>) value).iterator();

        Node node = context.getNode();
        Nodes ns = node.query("child::tr | child::thead/tr | child::tbody/tr");
        if (ns.size() == 0) {
            throw new UnstackedPluginException("Missing rows.");
        }
        Element head = null;
        for (int i = 0; i < ns.size(); i++) {
            Element tmp = (Element) ns.get(i);
            if (UtilNode.isIgnore(tmp)) {
                continue;
            }
            head = tmp;
            break;
        }
        if (head == null) {
            throw new UnstackedPluginException("Missing table header candidate.");
        }
        Nodes hs = head.query("child::th");
        if (hs.size() == 0) {
            throw new UnstackedPluginException("Missing header information (th's).");
        }
        UtilNode.setIgnore(head);

        IBlock block = context.peek();
        String pos = "index";
        int i = 1;
        boolean first = true;
        for (; i < ns.size(); i++) {
            Element row = (Element) ns.get(i);
            if (UtilNode.isIgnore(row)) {
                continue;
            }
            Nodes cs = row.query("child::td");
            if (hs.size() != cs.size()) {
                throw new UnstackedPluginException("Number of headers (" + hs.size() + ") is different of columns (" + cs.size() + ").");
            }
            for (int j = 0; j < cs.size(); j++) {
                Element h = (Element) hs.get(j);
                Element c = (Element) cs.get(j);
                for (int k = 0; k < h.getAttributeCount(); k++) {
                    Attribute att = h.getAttribute(k);
                    if (UtilNode.ATT_CSS.equals(att.getLocalName())) {
                        UtilNode.appendCss(c, att.getValue());
                    } else {
                        Attribute tmp = c.getAttribute(att.getLocalName());
                        if (tmp == null) {
                            c.addAttribute((Attribute) att.copy());
                        }
                    }
                }
            }
            context.push(context.newBlock(row, this));
            try {
                context.saveLocal(pos, String.valueOf(i - 1));
                if (ite.hasNext()) {
                    context.saveLocal(getName(), ite.next());
                    UtilPlugin.performChildren(row, context, result);
                } else {
                    if (first) {
                        result.addResult(Failure.INSTANCE, block, new UnstackedPluginException("Returned enumeration missing itens."));
                        first = false;
                    }
                }
            } finally {
                context.clearLocal(getName());
                context.clearLocal(pos);
                context.pop();
            }
        }
        INodeHolderFactory nf = SRServices.get(INodeHolderFactory.class);
        Element ele = (Element) node;
        first = true;
        while (ite.hasNext()) {
            if (first) {
                result.addResult(Failure.INSTANCE, context.peek(), new UnstackedPluginException("Returned enumeration has more elements than expected."));
                first = false;
            }
            try {
                Element tr = new Element("tr");
                context.push(context.newBlock(tr, this));
                context.saveLocal(pos, String.valueOf(i - 1));
                context.saveLocal(getName(), ite.next());
                for (int j = 0; j < hs.size(); j++) {
                    Element td = new Element("td");
                    INodeHolder newTd = nf.newHolder(td);
                    INodeHolder header = nf.newHolder(hs.get(j));
                    if (header.hasAttribute(INodeHolder.ATTRIBUTE_VALUE)) {
                        newTd.setAttribute(INodeHolder.ATTRIBUTE_VALUE, header.getAttribute(INodeHolder.ATTRIBUTE_VALUE));
                    }
                    if (header.hasAttribute(INodeHolder.ATTRIBUTE_PROPERTY)) {
                        newTd.setAttribute(INodeHolder.ATTRIBUTE_PROPERTY, header.getAttribute(INodeHolder.ATTRIBUTE_PROPERTY));
                    }
                    td.appendChild(String.valueOf(newTd.getObject(context, true)));
                    tr.appendChild(td);
                }
                ele.appendChild(tr);
                UtilPlugin.performChildren(tr, context, result);
            } finally {
                context.clearLocal(getName());
                context.clearLocal(pos);
                context.pop();
            }
            i++;
        }
        return ENext.SKIP;
    }
}
