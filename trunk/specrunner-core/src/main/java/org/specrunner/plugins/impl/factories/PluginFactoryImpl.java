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
package org.specrunner.plugins.impl.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.specrunner.SpecRunnerServices;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.util.UtilLog;

/**
 * Partial plugin factory implementation.
 * 
 * @author Thiago Santos.
 * 
 */
public abstract class PluginFactoryImpl implements IPluginFactory {

    /**
     * Types of plugins available by name.
     */
    protected Map<String, Class<? extends IPlugin>> types = new HashMap<String, Class<? extends IPlugin>>();
    /**
     * Alias of plugins.
     */
    protected Map<Class<? extends IPlugin>, String> aliases = new HashMap<Class<? extends IPlugin>, String>();
    /**
     * File to be loaded.
     */
    private String file;
    /**
     * Initialization status.
     */
    private boolean initialized = false;

    /**
     * Creates a factory loading the given file.
     * 
     * @param file
     *            The file to be loaded.
     */
    protected PluginFactoryImpl(String file) {
        if (file != null) {
            this.file = file;
        }
    }

    /**
     * Initialize the factory.
     * 
     * @throws PluginException
     *             On initialization errors.
     */
    @SuppressWarnings("unchecked")
    public void initialize() throws PluginException {
        if (!initialized) {
            try {
                Properties p = SpecRunnerServices.get(IPropertyLoader.class).load(file);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("properties=" + p);
                }
                for (Entry<Object, Object> e : p.entrySet()) {
                    Class<? extends IPlugin> c = (Class<? extends IPlugin>) Class.forName(String.valueOf(e.getValue()));
                    String key = String.valueOf(e.getKey()).toLowerCase();
                    if (types.get(key) != null && UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("replace(" + key + "," + c + ")");
                    } else {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("put(" + key + "," + c + ")");
                        }
                    }
                    types.put(key, c);
                    aliases.put(c, key);
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            initialized = true;
        }
    }

    @Override
    public String getAlias(Class<? extends IPlugin> type) {
        if (type == null) {
            return null;
        }
        return aliases.get(type);
    }
}
