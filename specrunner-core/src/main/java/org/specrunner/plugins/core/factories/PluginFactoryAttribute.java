/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.Map.Entry;

import nu.xom.Attribute;
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
 * An implementation which uses a attribute to return the plugin. To map a
 * plugin to a given attribute add a '<code>plugin_attribute.properties</code>'
 * file and add the name of the class.
 * <p>
 * i.e. <code>bean=test.PluginAny</code>, then just use attribute
 * <code>bean="anything"</code> anywhere in the specification.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryAttribute extends PluginFactoryImpl {

    /**
     * Creates the factory loading file 'sr_plugins_attribute.properties'.
     */
    public PluginFactoryAttribute() {
        super("sr_plugins_attribute.properties", PluginKind.ATTRIBUTE);
    }

    @Override
    public IPlugin newPlugin(Node node, IContext context) throws PluginException {
        initialize();
        if (node instanceof Element) {
            IPluginGroup result = new PluginGroupImpl();
            Element ele = (Element) node;
            // lookup using smaller set.
            if ((typeNamesToAlias.size() + templates.size()) < ele.getAttributeCount()) {
                byMapping(context, result, ele);
            } else {
                byAttribute(context, result, ele);
            }
            return result.getNormalized();
        }
        return PluginNop.emptyPlugin();
    }

    /**
     * Lookup plugin classes using mapped attributes as reference.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param ele
     *            The corresponding element.
     * @throws PluginException
     *             On plugin errors.
     */
    protected void byMapping(IContext context, IPluginGroup result, Element ele) throws PluginException {
        for (Entry<String, IPlugin> e : templates.entrySet()) {
            Attribute att = ele.getAttribute(e.getKey());
            if (att != null) {
                IPlugin create = UtilPlugin.create(context, e.getValue(), ele);
                create.setParent(this);
                result.add(create);
            }
        }
        for (Entry<String, Class<? extends IPlugin>> e : aliasToTypes.entrySet()) {
            Attribute att = ele.getAttribute(e.getKey());
            if (att != null) {
                Class<? extends IPlugin> c = aliasToTypes.get(e.getKey());
                if (c != null) {
                    IPlugin create = UtilPlugin.create(context, c, ele);
                    create.setParent(this);
                    result.add(create);
                }
            }
        }
    }

    /**
     * Lookup plugin classes using attribute enumeration as reference.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param ele
     *            The corresponding element.
     * @throws PluginException
     *             On plugin errors.
     */
    protected void byAttribute(IContext context, IPluginGroup result, Element ele) throws PluginException {
        for (int i = 0; i < ele.getAttributeCount(); i++) {
            Attribute att = ele.getAttribute(i);
            String name = att.getQualifiedName();
            IPlugin template = templates.get(name);
            IPlugin create = null;
            if (template != null) {
                create = UtilPlugin.create(context, template, ele);
            } else {
                Class<? extends IPlugin> c = getClass(name);
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
}
