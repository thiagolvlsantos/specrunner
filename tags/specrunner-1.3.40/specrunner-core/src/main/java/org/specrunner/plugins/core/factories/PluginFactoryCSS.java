/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.plugins.core.UtilPlugin;

/**
 * An implementation which uses the 'class' attribute to return the plugin. To
 * map a plugin to a given class add a '<code>plugin_css.properties</code>' file
 * and add the name of the class.
 * <p>
 * i.e. <code>ok=test.PluginAny</code>, then just use <code>class="ok"</code>
 * anywhere in the specification.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryCSS extends PluginFactoryImpl {

    /**
     * Attribute with CSS information.
     */
    public static final String ATTRIBUTE = "class";

    /**
     * Creates the factory loading file 'sr_plugins_css.properties'.
     */
    public PluginFactoryCSS() {
        super("sr_plugins_css.properties", PluginKind.CSS);
    }

    @Override
    public IPlugin newPlugin(Node node, IContext context) throws PluginException {
        initialize();
        if (node instanceof Element) {
            IPluginGroup result = new PluginGroupImpl();
            Element ele = (Element) node;
            Node att = ele.getAttribute(ATTRIBUTE);
            if (att != null) {
                String[] pcs = att.getValue().split(" ");
                for (String s : pcs) {
                    String p = s.toLowerCase();
                    IPlugin create = null;
                    IPlugin template = templates.get(p);
                    if (template != null) {
                        create = UtilPlugin.create(context, template, ele);
                    } else {
                        Class<? extends IPlugin> c = getClass(p);
                        if (c != null) {
                            create = UtilPlugin.create(context, c, ele);
                        }
                    }
                    if (create != null) {
                        create.setParent(this);
                        result.add(create);
                    }
                }
            }
            return result.getNormalized();
        }
        return PluginNop.emptyPlugin();
    }
}