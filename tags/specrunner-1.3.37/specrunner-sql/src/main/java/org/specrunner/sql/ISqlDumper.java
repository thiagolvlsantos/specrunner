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
package org.specrunner.sql;

import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Abstraction for SQL dumper.
 * 
 * @author Thiago Santos.
 * 
 */
public interface ISqlDumper {

    /**
     * Perform some actions in a database.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The test result.
     * @param sql
     *            Prepared SQL to replace.
     * @param arguments
     *            The SQL arguments.
     * @throws PluginException
     *             On dump errors.
     */
    void dump(IContext context, IResultSet result, String sql, Map<Integer, Object> arguments) throws PluginException;
}
