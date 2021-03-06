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

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.plugins.core.text.PluginReplacer;
import org.specrunner.plugins.core.text.PluginReplacerMap;

import nu.xom.Node;
import nu.xom.Text;

/**
 * A factory for Text elements.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryText implements IPluginFactory {

    /**
     * Concurrent instance of plugin.
     */
    protected static ThreadLocal<IPlugin> instance = new ThreadLocal<IPlugin>() {
        @Override
        protected IPlugin initialValue() {
            IPluginGroup group = new PluginGroupImpl();
            group.add(new PluginReplacerMap());
            group.add(new PluginReplacer());
            return group;
        };
    };

    @Override
    public void initialize() throws PluginException {
    }

    @Override
    public Class<? extends IPlugin> getClass(String alias) throws PluginException {
        return null;
    }

    @Override
    public String getAlias(Class<? extends IPlugin> type) {
        return null;
    }

    @Override
    public IPluginFactory bind(PluginKind type, String alias, IPlugin plugin) {
        if (PluginKind.TEXT.equals(type)) {
            ((IPluginGroup) instance.get()).add(plugin);
        }
        return this;
    }

    @Override
    public IPlugin newPlugin(Node node, IContext context) {
        if (node instanceof Text) {
            // reuse the instance replacer.
            return instance.get();
        }
        return PluginNop.emptyPlugin();
    }

    @Override
    public boolean finalizePlugin(Node source, IContext context, IPlugin plugin) throws PluginException {
        return false;
    }
}
