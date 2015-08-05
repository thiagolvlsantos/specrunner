/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.plugins.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

/**
 * Check if a given list of objects is the same of object manager. The object
 * output list has the same format of input.
 * 
 * @param <T>
 *            Manager type.
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginObjectCompareAll<T> extends AbstractPluginObjectCompare<T> {

    /**
     * @param selector
     *            Object selector. Create a output comparison.
     */
    public AbstractPluginObjectCompareAll(IObjectSelector<T> selector) {
        super(selector);
    }

    @Override
    protected void readData(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        List<Object> objects = null;
        try {
            objects = new ArrayList<Object>(selector.all(this, context, result));
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        if (table.getRowCount() - 1 > objects.size()) {
            result.addResult(Failure.INSTANCE, context.newBlock(table.getNode(), this), new PluginException("Number of expected elements (" + (table.getRowCount() - 1) + ") is higher than the objects of type '" + getType() + "' (" + objects.size() + ")"));
        }
        for (int i = 1; i < table.getRowCount(); i++) {
            RowAdapter row = table.getRow(i);
            try {
                Object instance = processLine(context, row, result);
                List<Object> selected = selector.select(this, context, instance, row, result);
                if (selected.isEmpty()) {
                    throw new PluginException("Element at line '" + (i - 1) + "' not found in general collection.");
                }
                if (selected.size() > 1) {
                    throw new PluginException("More than 1 element found for line '" + (i - 1) + "' in general collection.");
                }
                removeFromCollection(objects, selected.get(0));
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), e);
            }
        }
        if (!objects.isEmpty()) {
            StringBuilder msg = new StringBuilder("Extra objects found for table.");
            for (int i = 0; i < objects.size(); i++) {
                msg.append("\n" + i + "." + objects.get(i));
            }
            result.addResult(Failure.INSTANCE, context.newBlock(table.getNode(), this), new PluginException(msg.toString()));
        }
    }

    /**
     * Remove object found of collection.
     * 
     * @param objects
     *            List of expected objects in memory/database/etc.
     * @param selected
     *            Element to remove.
     */
    protected void removeFromCollection(List<Object> objects, Object selected) {
        objects.remove(selected);
    }
}
