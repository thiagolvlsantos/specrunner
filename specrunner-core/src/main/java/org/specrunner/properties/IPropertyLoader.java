/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.properties;

import java.util.List;
import java.util.Properties;

/**
 * Generic property loader. Loads properties from different property files in
 * the classpath. i.e. if a 'plugin_css.properties' load is requested, all files
 * 'plugin_css.properties' in classpath will be loaded.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPropertyLoader {

    /**
     * Load a set of properties with the given name.
     * 
     * @param file
     *            The file name.
     * @return The merge of all properties with this name in classpath.
     * @throws PropertyLoaderException
     *             On reading properties errors.
     */
    List<Properties> load(String file) throws PropertyLoaderException;
}
