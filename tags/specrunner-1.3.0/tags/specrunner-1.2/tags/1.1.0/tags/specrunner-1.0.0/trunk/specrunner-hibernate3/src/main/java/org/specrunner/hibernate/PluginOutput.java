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
package org.specrunner.hibernate;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.objects.AbstractPluginObjectCompare;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.impl.CellAdapter;
import org.specrunner.util.impl.RowAdapter;

public class PluginOutput extends AbstractPluginObjectCompare {

    private Session session;

    @Override
    public boolean isMapped() {
        return false;
    }

    @Override
    public Object selectUnique(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        SessionFactory sf = PluginSessionFactory.getSessionFactory(context, getName());
        session = sf.openSession();
        Criteria c = session.createCriteria(instance.getClass());
        String[] keyFields = reference.split(",");
        for (int i = 0; i < keyFields.length; i++) {
            c.add(Restrictions.eq(keyFields[i], PropertyUtils.getProperty(instance, keyFields[i])));
        }
        return c.uniqueResult();
    }

    @Override
    public void release() throws Exception {
        if (session != null) {
            session.close();
        }
    }

    @Override
    public void compare(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception {
        for (Field f : fields) {
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
            if (comparator == null) {
                Class<?> type = currentInstance.getClass();
                comparator = cf.get(type);
            }
            // by default
            if (comparator == null) {
                comparator = cf.getDefaultComparator();
            }
            if (comparator.equals(currentInstance, currentBase)) {
                result.addResult(Status.SUCCESS, context.newBlock(row.getCell(f.getIndex()).getElement(), this));
            } else {
                IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(currentInstance), String.valueOf(currentBase));
                result.addResult(Status.FAILURE, context.newBlock(row.getCell(f.getIndex()).getElement(), this), new DefaultAlignmentException(aligner));
            }
        }
    }
}
