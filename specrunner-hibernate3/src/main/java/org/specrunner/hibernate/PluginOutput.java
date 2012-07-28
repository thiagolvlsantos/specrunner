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

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.specrunner.context.IContext;
import org.specrunner.objects.AbstractPluginObjectCompare;
import org.specrunner.result.IResultSet;
import org.specrunner.util.impl.RowAdapter;

/**
 * Check if a given list of objects is present in database. The object output
 * list has the same format of input.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginOutput extends AbstractPluginObjectCompare {

    /**
     * The object session factory.
     */
    protected Session session;

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> select(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        SessionFactory sf = PluginSessionFactory.getSessionFactory(context, getName());
        session = sf.openSession();
        Criteria c = session.createCriteria(instance.getClass());
        String[] keyFields = reference.split(",");
        for (int i = 0; i < keyFields.length; i++) {
            c.add(Restrictions.eq(keyFields[i], PropertyUtils.getProperty(instance, keyFields[i])));
        }
        return c.list();
    }

    @Override
    public void release() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}