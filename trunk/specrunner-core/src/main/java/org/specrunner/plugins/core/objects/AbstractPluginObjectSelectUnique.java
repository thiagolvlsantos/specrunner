/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Warning;
import org.specrunner.util.xom.node.RowAdapter;

/**
 * Performs something on a selected object.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source type.
 */
public abstract class AbstractPluginObjectSelectUnique<T> extends AbstractPluginObjectSelect<T> {

    /**
     * Create a select plugin, provided an object selector.
     * 
     * @param selector
     *            The selector.
     */
    public AbstractPluginObjectSelectUnique(IObjectSelector<T> selector) {
        super(selector);
    }

    @Override
    public void processList(IContext context, Object instance, RowAdapter row, IResultSet result, List<Object> list) throws Exception {
        if (list.isEmpty()) {
            addError(context, row, result, new PluginException("None element found. XML:" + row.toXML()));
            return;
        }
        if (list.size() > 1) {
            addError(context, row, result, new PluginException("More than one element found. XML:" + row.toXML()));
            return;
        }
        Object base = list.get(0);
        if (base == null) {
            addError(context, row, result, new PluginException("This item is not present in object repository. XML:" + row.toXML()));
        } else {
            perform(context, base, instance, row, result);
        }
    }

    /**
     * Add a error to a comparison.
     * 
     * @param context
     *            The context.
     * @param row
     *            The row.
     * @param result
     *            The result set.
     * @param e
     *            The error.
     */
    protected void addError(IContext context, RowAdapter row, IResultSet result, Exception e) {
        for (int i = 0; i < row.getCellsCount(); i++) {
            result.addResult(i == 0 ? Failure.INSTANCE : Warning.INSTANCE, context.newBlock(row.getCell(i).getNode(), this), i == 0 ? e : null);
        }
    }

    /**
     * Perform something on a database object.
     * 
     * @param context
     *            The test context.
     * @param base
     *            The object version from repository.
     * @param instance
     *            The object version from specification.
     * @param row
     *            The row which give origin to the 'instance'.
     * @param result
     *            The result set.
     * @throws Exception
     *             On processing errors.
     */
    public abstract void perform(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception;
}