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
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Warning;
import org.specrunner.util.UtilLog;
import org.specrunner.util.impl.RowAdapter;

/**
 * Performs something on a selected object.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginObjectSelect extends AbstractPluginObject {

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
        try {
            List<Object> list = select(context, instance, row, result);
            if (list.isEmpty()) {
                addError(context, row, result, new PluginException("None element found. XML:" + row.getElement().toXML()));
                return;
            }
            if (list.size() > 1) {
                addError(context, row, result, new PluginException("More than one element found. XML:" + row.getElement().toXML()));
                return;
            }
            Object base = list.get(0);
            if (base == null) {
                addError(context, row, result, new PluginException("This register is not present in object repository. XML:" + row.getElement().toXML()));
            } else {
                perform(context, base, instance, row, result);
            }

        } finally {
            release();
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
    private void addError(IContext context, RowAdapter row, IResultSet result, Exception e) {
        for (int i = 0; i < row.getCellsCount(); i++) {
            result.addResult(i == 0 ? Failure.INSTANCE : Warning.INSTANCE, context.newBlock(row.getCell(i).getElement(), this), i == 0 ? e : null);
        }
    }

    /**
     * Performs a select on object repository to compare with the reference.
     * 
     * @param context
     *            The test context.
     * @param instance
     *            The object to be compared with repository version.
     * @param row
     *            The row which was the source for object creation.
     * @param result
     *            The result set.
     * @return The corresponding objects from repository.
     * @throws Exception
     *             On selecion errors.
     */
    public abstract List<Object> select(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception;

    /**
     * Release comparison resources. i.e. For Hibernate repositories free
     * sessions in use for comparison.
     * 
     * @throws Exception
     *             On release errors.
     */
    public abstract void release() throws Exception;

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