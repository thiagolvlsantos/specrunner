package org.specrunner.plugins.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilEvaluator;

public class PluginMap extends AbstractPluginScoped {

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        final List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
        Nodes ths = node.query("descendant::th");
        Nodes trs = node.query("descendant::tr");
        for (int i = 0; i < trs.size(); i++) {
            if (i == 0) { // ignore headers.
                continue;
            }
            Node tr = trs.get(i);
            Nodes tds = tr.query("descendant::td");
            if (tds.size() != ths.size()) {
                throw new PluginException("Number of headers '" + ths.size() + "' is diferent from line columns '" + tds.size() + "'.");
            }
            Map<String, Object> line = new HashMap<String, Object>();
            data.add(line);
            for (int j = 0; j < tds.size(); j++) {
                line.put(UtilEvaluator.START_DATA + ths.get(j).getValue().trim() + UtilEvaluator.END, tds.get(j));
            }
        }
        IDataMap<Object> map = new IDataMap<Object>() {
            @Override
            public int getTotal() {
                return data.size();
            }

            @Override
            public Iterator<Map<String, Object>> iterator() {
                return data.iterator();
            }
        };
        saveLocal(context, getName(), map);
        for (int i = 0; i < ths.size(); i++) {
            result.addResult(Status.SUCCESS, context.newBlock(ths.get(i), this));
        }
        return ENext.SKIP;
    }
}