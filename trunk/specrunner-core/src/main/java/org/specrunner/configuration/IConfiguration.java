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
package org.specrunner.configuration;

import java.util.Map;

/**
 * Stand for a local configuration to be used in a given execution. The
 * configuration is different from a feature setting, a configuration is like a
 * set of features local to the test execution, and a features settings are
 * global to all tests.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConfiguration extends Map<String, Object> {

    /**
     * Add a feature to the configuration.
     * 
     * @param feature
     *            The feature name.
     * @param value
     *            The feature value.
     * @return The configuration itself.
     */
    IConfiguration add(String feature, Object value);

    /**
     * Get a value from map, if not found, result is 'defaultValue'.
     * 
     * @param key
     *            A name.
     * @param defaultValue
     *            The default value.
     * @return The key value, or defaultValue, if not found.
     */
    Object get(String key, Object defaultValue);
}
