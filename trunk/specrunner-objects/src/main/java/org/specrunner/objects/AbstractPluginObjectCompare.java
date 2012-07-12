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

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.impl.CellAdapter;
import org.specrunner.util.impl.RowAdapter;

/**
 * Performs object after some system iteraction.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginObjectCompare extends AbstractPluginObject {

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
                compare(context, base, instance, row, result);
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
     * Default comparison processing.
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
     *             On comparison errors.
     */
    public void compare(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception {
        for (Field f : fields) {
            if (f.isIgnore()) {
                continue;
            }
            Object currentInstance = instance;
            for (int i = 0; i < f.getNames().length; i++) {
                currentInstance = PropertyUtils.getProperty(currentInstance, f.getNames()[i]);
                if (currentInstance == null) {
                    break;
                }
            }
            Object currentBase = base;
            for (int i = 0; i < f.getNames().length; i++) {
                currentBase = PropertyUtils.getProperty(currentBase, f.getNames()[i]);
                if (currentBase == null) {
                    break;
                }
            }

            // lookup for best match comparator
            IComparatorManager cf = SpecRunnerServices.get(IComparatorManager.class);
            IComparator comparator = null;
            // specific cell comparator
            CellAdapter cell = row.getCell(f.getIndex());
            if (cell.hasAttribute("comparator")) {
                comparator = cf.get(cell.getAttribute("comparator"));
            }
            // by column comparator
            if (comparator == null && f.getComparator() != null) {
                comparator = cf.get(f.getComparator());
            }
            // by object type
            if (comparator == null && currentInstance != null) {
                Class<?> type = currentInstance.getClass();
                comparator = cf.get(type);
            }
            // by default
            if (comparator == null) {
                comparator = cf.getDefaultComparator();
            }
            if (comparator.equals(currentInstance, currentBase)) {
                result.addResult(Success.INSTANCE, context.newBlock(row.getCell(f.getIndex()).getElement(), this));
            } else {
                IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(currentInstance), String.valueOf(currentBase));
                result.addResult(Failure.INSTANCE, context.newBlock(row.getCell(f.getIndex()).getElement(), this), new DefaultAlignmentException(aligner));
            }
        }
    }
}