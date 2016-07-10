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
package org.specrunner.parameters;

import org.specrunner.plugins.PluginException;

/**
 * Defines a factory of feature access information.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IAccessFactory {

    /**
     * Feature to accept children properties as null a parent object in path is
     * null.
     */
    String FEATURE_PROPERTY_ACCEPT_NULL_PATH = IAccessFactory.class.getName() + ".acceptNullPathProperty";
    /**
     * Default is to accept null paths as null.
     */
    Boolean DEFAULT_PROPERTY_ACCEPT_NULL_PATH = Boolean.TRUE;
    /**
     * Feature to accept invalid property paths as null.
     */
    String FEATURE_PROPERTY_INVALID_PATH_AS_NULL = IAccessFactory.class.getName() + ".invalidPathAsNullProperty";
    /**
     * Default is to not accept invalid paths.
     */
    Boolean DEFAULT_PROPERTY_INVALID_PATH_AS_NULL = Boolean.FALSE;

    /**
     * Create access information for a given object and a given feature.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature name.
     * @return The access abstraction.
     */
    IAccess newAccess(Object target, String name);

    /**
     * Get the object value for <code>str</code> path in <code>object</code>.
     * 
     * @param object
     *            A object to inspect.
     * @param property
     *            A property path.
     * @throws PluginException
     *             On lookup errors.
     * @return null, depending on property accept features, false, otherwise.
     */
    Object getProperty(Object object, String property) throws PluginException;
}