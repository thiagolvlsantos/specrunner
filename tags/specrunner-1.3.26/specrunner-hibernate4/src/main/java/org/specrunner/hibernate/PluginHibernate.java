/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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

import org.hibernate.SessionFactory;
import org.specrunner.context.IContext;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.RowAdapter;

/**
 * Generic Hibernate plugin. To write Hibernate plugins override method
 * <code>isMapped()</code> and <code>action(...)</code>.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class PluginHibernate extends AbstractPluginObject {

    /**
     * The configuration name.
     */
    private String configuration;

    /**
     * The configuration name.
     * 
     * @return The name.
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * Set configuration name.
     * 
     * @param configuration
     *            The name.
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    /**
     * Process a Hibernate action.
     * 
     * @param context
     *            The context.
     * @param instance
     *            The instance ready to use. All object where set, and related
     *            objects assembled.
     * @param row
     *            The row.
     * @param result
     *            The result set.
     * @throws Exception
     *             On processing errors.
     */
    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        action(context, instance, row, result, PluginSessionFactory.getSessionFactory(context, configuration));
    }

    /**
     * This method can be and should be overridden to perform save, comparison,
     * etc for updates.
     * 
     * @param context
     *            The test context.
     * @param instance
     *            The object instance.
     * @param row
     *            The row of object.
     * @param result
     *            The result set.
     * @param sf
     *            The session factory.
     * @throws Exception
     *             On exception errors.
     */
    protected abstract void action(IContext context, Object instance, RowAdapter row, IResultSet result, SessionFactory sf) throws Exception;
}