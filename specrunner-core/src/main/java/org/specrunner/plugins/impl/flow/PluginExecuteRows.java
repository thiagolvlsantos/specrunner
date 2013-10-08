package org.specrunner.plugins.impl.flow;

import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.runner.IFilter;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.RunnerException;
import org.specrunner.runner.impl.FilterDefault;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

/**
 * Perform table actions.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginExecuteRows extends PluginIterable {

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
                row.addAttribute(att);
            }
            row.addAttribute(new Attribute("class", "execute"));
            row.addAttribute(new Attribute("onstart", "true"));

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
            IRunner runner = context.getRunner();
            IFilter filter = runner.getFilter();
            FilterDefault tmp = FilterDefault.INSTANCE.get();
            List<? extends ActionType> types = tmp.getDisabledTypes();
            boolean show = tmp.isShowMessage();
            try {
                context.saveLocal(pos, String.valueOf(i - 1));
                tmp.setEnabledTypes(Arrays.asList(Command.INSTANCE));
                tmp.setShowMessage(false);
                runner.setFilter(tmp);
                UtilPlugin.performChildren(row, context, result);
                tmp.setEnabledTypes(null);
                runner.run(row, context, result);
            } catch (RunnerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e.getMessage(), e);
            } finally {
                tmp.setDisabledTypes(types);
                tmp.setShowMessage(show);
                runner.setFilter(filter);
                context.clearLocal(pos);
                context.pop();
            }
        }
        return ENext.SKIP;
    }
}
