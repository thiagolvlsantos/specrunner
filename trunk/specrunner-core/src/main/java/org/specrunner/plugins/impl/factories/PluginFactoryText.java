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
package org.specrunner.plugins.impl.factories;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.impl.PluginGroupImpl;
import org.specrunner.plugins.impl.PluginNop;
import org.specrunner.plugins.impl.text.PluginReplacer;
import org.specrunner.plugins.impl.text.PluginReplacerList;
import org.specrunner.plugins.impl.text.PluginReplacerMap;

/**
 * A factory for Text elements.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryText implements IPluginFactory {

    private static ThreadLocal<IPlugin> instance = new ThreadLocal<IPlugin>() {
        @Override
        protected IPlugin initialValue() {
            IPluginGroup group = new PluginGroupImpl();
            group.add(new PluginReplacerList());
            group.add(new PluginReplacerMap());
            group.add(new PluginReplacer());
            return group;
        };
    };

    @Override
    public IPlugin newPlugin(Node node, IContext context) {
        if (node instanceof Text) {
            // reuse the instance replacer.
            return instance.get();
        }
        return PluginNop.emptyPlugin();
    }

}
