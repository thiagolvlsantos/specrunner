package org.specrunner.plugins.impl.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.impl.CellAdapter;
import org.specrunner.util.impl.RowAdapter;
import org.specrunner.util.impl.TableAdapter;

public class PluginMap extends AbstractPluginTable {

    private Boolean after = false;

    public Boolean getAfter() {
        return after;
    }

    public void setAfter(Boolean after) {
        this.after = after;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        if (!after) {
            process(context, result, tableAdapter);
            return ENext.SKIP;
        } else {
            return ENext.DEEP;
        }
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        if (after) {
            process(context, result, tableAdapter);
        }
    }

    private void process(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        final List<Map<String, Node>> data = new LinkedList<Map<String, Node>>();
        RowAdapter ths = tableAdapter.getRow(0);
        List<RowAdapter> trs = tableAdapter.getRows();
        for (int i = 0; i < tableAdapter.getRowCount(); i++) {
            if (i == 0) { // ignore headers.
                continue;
            }
            RowAdapter tr = trs.get(i);
            if (tr.getCellsCount() != ths.getCellsCount()) {
                throw new PluginException("Number of headers '" + ths.getCellsCount() + "' is diferent from line columns '" + tr.getCellsCount() + "'.");
            }
            List<CellAdapter> tds = tr.getCells();
            Map<String, Node> line = new HashMap<String, Node>();
            data.add(line);
            for (int j = 0; j < tds.size(); j++) {
                Element e = tds.get(j).getElement();
                line.put(UtilEvaluator.START_DATA + j + UtilEvaluator.END, e);
                line.put(UtilEvaluator.START_DATA + ths.getCell(j).getElement().getValue().trim() + UtilEvaluator.END, e);
            }
        }
        IDataMap<Node> map = new IDataMap<Node>() {
            @Override
            public int getTotal() {
                return data.size();
            }

            @Override
            public Iterator<Map<String, Node>> iterator() {
                return data.iterator();
            }
        };
        saveLocal(context, getName(), map);
        for (int i = 0; i < ths.getCellsCount(); i++) {
            result.addResult(Status.SUCCESS, context.newBlock(ths.getCell(i).getElement(), this));
        }
    }
}