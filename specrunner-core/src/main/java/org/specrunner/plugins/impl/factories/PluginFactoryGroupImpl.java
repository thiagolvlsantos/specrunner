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

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginFactoryGroup;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.PluginGroupImpl;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default factory group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryGroupImpl extends CompositeImpl<IPluginFactoryGroup, IPluginFactory> implements IPluginFactoryGroup {

    @Override
    public IPlugin newPlugin(Node node, IContext context) throws PluginException {
        IPluginGroup group = new PluginGroupImpl();
        for (IPluginFactory pf : getChildren()) {
            IPlugin p = pf.newPlugin(node, context);
            if (p != null) {
                group.add(p);
            }
        }
        return group.getNormalized();
    }

    @Override
    public String getAlias(Class<? extends IPlugin> type) {
        String result = null;
        for (IPluginFactory pf : getChildren()) {
            result = pf.getAlias(type);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public IPluginFactory bind(String type, String alias, IPlugin plugin) throws PluginException {
        for (IPluginFactory pf : getChildren()) {
            pf.bind(type, alias, plugin);
        }
        return this;
    }

    @Override
    public boolean finalizePlugin(Node source, IContext context, IPlugin plugin) throws PluginException {
        for (IPluginFactory pf : getChildren()) {
            if (pf.finalizePlugin(source, context, plugin)) {
                return true;
            }
        }
        return false;
    }
}