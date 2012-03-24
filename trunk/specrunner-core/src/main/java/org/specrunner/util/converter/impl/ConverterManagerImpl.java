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
package org.specrunner.util.converter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.specrunner.SpecRunnerServices;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.util.UtilLog;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;

/**
 * Default converter manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ConverterManagerImpl implements IConverterManager {

    /**
     * Map of converters.
     */
    protected Map<String, IConverter> converters = new HashMap<String, IConverter>();
    /**
     * Initialization flag.
     */
    protected boolean initialized = false;

    /**
     * Initialize manager.
     */
    public void initialize() {
        if (!initialized) {
            try {
                Properties p = SpecRunnerServices.get(IPropertyLoader.class).load("plugin_converter.properties");
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("features=" + p);
                }
                for (Entry<Object, Object> e : p.entrySet()) {
                    String key = String.valueOf(e.getKey());
                    @SuppressWarnings("unchecked")
                    Class<? extends IConverter> c = (Class<? extends IConverter>) Class.forName(p.getProperty(key));
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("put(" + key + "," + c + ")");
                    }
                    converters.put(key, c.newInstance());
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
            initialized = true;
        }
    }

    @Override
    public void bind(String name, IConverter iConverter) {
        initialize();
        converters.put(name, iConverter);
    }

    @Override
    public IConverter get(String name) {
        initialize();
        return converters.get(name);
    }
}