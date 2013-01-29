/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.objects;

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.RowAdapter;

/**
 * Performs something on a selected object.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source type.
 */
public abstract class AbstractPluginObjectSelect<T> extends AbstractPluginObject {

    /**
     * The default object selector.
     */
    protected IObjectSelector<T> selector;

    /**
     * The source.
     */
    protected T source;

    /**
     * Create a select plugin, provided an object selector.
     * 
     * @param selector
     *            The selector.
     */
    public AbstractPluginObjectSelect(IObjectSelector<T> selector) {
        this.selector = selector;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public boolean isMapped() {
        return false;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("KEYS>" + reference);
        }
        source = selector.getSource(this, context);
        try {
            List<Object> list = selector.select(this, context, instance, row, result);
            processList(context, instance, row, result, list);
        } finally {
            selector.release();
        }
    }

    /**
     * Process the list of objects recovered.
     * 
     * @param context
     *            The context.
     * @param instance
     *            The row instance.
     * @param row
     *            The row.
     * @param result
     *            The result set.
     * @param list
     *            The list of objects recovered.
     * @throws Exception
     *             On processing errors.
     */
    public abstract void processList(IContext context, Object instance, RowAdapter row, IResultSet result, List<Object> list) throws Exception;
}