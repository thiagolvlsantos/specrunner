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

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SRServices;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.IComparatorManager;
import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.RowAdapter;

/**
 * Performs comparison of objects.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source type.
 */
public abstract class AbstractPluginObjectCompare<T> extends AbstractPluginObjectSelectUnique<T> {

    /**
     * Create a plugin comparator, provided an object selector.
     * 
     * @param selector
     *            The object selector.
     */
    public AbstractPluginObjectCompare(IObjectSelector<T> selector) {
        super(selector);
    }

    @Override
    public void perform(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception {
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
            IComparatorManager cf = SRServices.getComparatorManager();
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
                comparator = cf.getDefault();
            }
            comparator.initialize();
            if (comparator.match(currentInstance, currentBase)) {
                result.addResult(Success.INSTANCE, context.newBlock(row.getCell(f.getIndex()).getNode(), this));
            } else {
                result.addResult(Failure.INSTANCE, context.newBlock(row.getCell(f.getIndex()).getNode(), this), new DefaultAlignmentException(String.valueOf(currentInstance), String.valueOf(currentBase)));
            }
        }
    }
}