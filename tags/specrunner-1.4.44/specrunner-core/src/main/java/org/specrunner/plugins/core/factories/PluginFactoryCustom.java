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
package org.specrunner.plugins.core.factories;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.plugins.core.UtilPlugin;

/**
 * Returns a plugin based on a 'custom' attribute.
 * <p>
 * i.e. custom="test.PluginSysout",
 * <p>
 * or based on a file ' <code>plugin_custom.properties</code> , i.e. add
 * <code>new=test.PluginCustomized</code>, and use <code>custom="new"</code>
 * anywhere in the specification.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryCustom extends PluginFactoryImpl {

    /**
     * Attribute with custom plugin information.
     */
    public static final String ATTRIBUTE = "custom";

    /**
     * Creates the factory loading file 'sr_plugins_custom.properties'.
     */
    public PluginFactoryCustom() {
        super("sr_plugins_custom.properties", PluginKind.CUSTOM);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IPlugin newPlugin(Node node, IContext context) throws PluginException {
        initialize();
        if (node instanceof Element) {
            Element ele = (Element) node;
            Node att = ele.getAttribute(ATTRIBUTE);
            if (att != null) {
                String clazz = att.getValue();
                IPlugin template = templates.get(clazz);
                if (template != null) {
                    IPlugin create = UtilPlugin.create(context, template, ele);
                    create.setParent(this);
                    return create;
                } else {
                    Class<? extends IPlugin> c = getClass(clazz);
                    try {
                        if (c == null) {
                            c = (Class<? extends IPlugin>) Class.forName(clazz);
                        }
                        IPlugin create = UtilPlugin.create(context, c, ele);
                        create.setParent(this);
                        return create;
                    } catch (ClassNotFoundException e) {
                        throw new PluginException("Plugin class " + clazz + " not found.", e);
                    } catch (ClassCastException e) {
                        throw new PluginException("Plugin class " + clazz + " is not an instance of IPlugin.", e);
                    }
                }
            }
        }
        return PluginNop.emptyPlugin();
    }
}
