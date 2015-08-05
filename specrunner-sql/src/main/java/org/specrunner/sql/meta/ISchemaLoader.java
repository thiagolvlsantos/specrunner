/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.sql.meta;

import org.specrunner.context.IContext;

/**
 * Load the schema information from somewhere.
 * 
 * @author Thiago Santos.
 * 
 */
public interface ISchemaLoader {

    /**
     * Load a Schema object from a object.
     * 
     * @param context
     *            Test context.
     * @param source
     *            The loader source of information.
     * @return The schema.
     * @throws SchemaException
     *             On loading errors.
     */
    Schema load(IContext context, Object source) throws SchemaException;
}
