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
package org.specrunner.plugins.core.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.util.UtilLog;

import nu.xom.Element;
import nu.xom.Node;

/**
 * Partial plugin factory implementation.
 * 
 * @author Thiago Santos.
 * 
 */
public abstract class PluginFactoryImpl implements IPluginFactory {

    /**
     * Kind of factory.
     */
    protected PluginKind kind;
    /**
     * Map from alias to class name.
     */
    protected Map<String, String> aliasToTypeNames = new HashMap<String, String>();
    /**
     * Map from alias to types.
     */
    protected Map<String, Class<? extends IPlugin>> aliasToTypes = new HashMap<String, Class<? extends IPlugin>>();
    /**
     * Map from alias to templates.
     */
    protected Map<String, IPlugin> templates = new HashMap<String, IPlugin>();
    /**
     * Map from alias to names.
     */
    protected Map<String, String> typeNamesToAlias = new HashMap<String, String>();
    /**
     * File to be loaded.
     */
    protected String file;
    /**
     * Initialization status.
     */
    protected boolean initialized = false;

    /**
     * Creates a factory loading the given file.
     * 
     * @param file
     *            The file to be loaded.
     * @param kind
     *            The factory kind.
     */
    protected PluginFactoryImpl(String file, PluginKind kind) {
        if (file != null) {
            this.file = file;
        }
        this.kind = kind;
    }

    /**
     * Get the kind to factory.
     * 
     * @return The factory kind.
     */
    public PluginKind getKind() {
        return kind;
    }

    @Override
    public void initialize() throws PluginException {
        if (!initialized) {
            try {
                List<Properties> list = SRServices.get(IPropertyLoader.class).load(file);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("properties list=" + list);
                }
                for (Properties p : list) {
                    for (Entry<Object, Object> e : p.entrySet()) {
                        String key = String.valueOf(e.getKey()).toLowerCase();
                        String value = String.valueOf(e.getValue());
                        aliasToTypeNames.put(key, value);
                        typeNamesToAlias.put(value, key);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("properties loaded: " + file);
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends IPlugin> getClass(String alias) throws PluginException {
        initialize();
        String type = aliasToTypeNames.get(alias);
        if (type == null) {
            return null;
        }
        Class<? extends IPlugin> c = aliasToTypes.get(alias);
        if (c == null) {
            try {
                c = (Class<? extends IPlugin>) Class.forName(type);
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            aliasToTypes.put(alias, c);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("put(" + alias + "," + c + ")");
            }
        }
        return c;
    }

    @Override
    public String getAlias(Class<? extends IPlugin> type) throws PluginException {
        if (type == null) {
            return null;
        }
        initialize();
        return typeNamesToAlias.get(type.getName());
    }

    @Override
    public IPluginFactory bind(PluginKind kind, String alias, IPlugin plugin) throws PluginException {
        if (kind == null || alias == null || plugin == null) {
            throw new PluginException("Ivalid bind, all arguments must be not null. Current bind (" + kind + "," + alias + "," + plugin + ")");
        }
        if (test(kind)) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("put(" + alias + "," + plugin + ")");
            }
            templates.put(alias.toLowerCase(), plugin);
        }
        return this;
    }

    /**
     * Test if the factory accept the plugin.
     * 
     * @param type
     *            The plugin type.
     * @return <code>true</code>, if accept the <code>type</code>, false,
     *         otherwise.
     */
    protected boolean test(PluginKind type) {
        return kind.equals(type);
    }

    @Override
    public boolean finalizePlugin(Node source, IContext context, IPlugin plugin) throws PluginException {
        if (plugin == null) {
            return false;
        }
        if (plugin instanceof PluginNop) {
            return false;
        }
        if (source instanceof Element && plugin.getParent() == this) {
            Element ele = (Element) source;
            UtilPlugin.destroy(context, plugin, ele);
            return true;
        }
        return false;
    }
}
