/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.Collection;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.node.RowAdapter;

/**
 * Perform object searches.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source of selection.
 */
public interface IObjectSelector<T> {

    /**
     * The source for object selection.
     * 
     * @param caller
     *            The plugin.
     * @param context
     *            The testing context.
     * @return The source.
     * @throws Exception
     *             On selection errors.
     */
    T getSource(AbstractPluginObject caller, IContext context) throws Exception;

    /**
     * Performs a select all object of a given type.
     * 
     * @param caller
     *            The caller plugin.
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @return The corresponding objects from repository.
     * @throws Exception
     *             On selection errors.
     */
    Collection<Object> all(AbstractPluginObject caller, IContext context, IResultSet result) throws Exception;

    /**
     * Performs a select on object repository to compare with the reference.
     * 
     * @param caller
     *            The caller plugin.
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
     *             On selection errors.
     */
    List<Object> select(AbstractPluginObject caller, IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception;

    /**
     * Release comparison resources. i.e. For Hibernate repositories free
     * sessions in use for comparison.
     * 
     * @throws Exception
     *             On release errors.
     */
    void release() throws Exception;
}
