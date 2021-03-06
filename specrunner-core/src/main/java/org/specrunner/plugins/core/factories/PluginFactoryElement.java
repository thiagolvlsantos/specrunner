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
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.plugins.core.UtilPlugin;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/**
 * Creates a plugin by element. To bind an element add a '
 * <code>plugin_element.properties</code>' file and map the tag.
 * <p>
 * i.e. a map of <code>br=test.PluginNewLine</code> will perfome the plugin each
 * tag <code>&lt;br&gt;</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFactoryElement extends PluginFactoryImpl {

    /**
     * Separator of name field.
     */
    protected static final char SEPARATOR = '-';

    /**
     * Attribute to create a element as another type. Use <code>as='type'</code>
     * to refer the actual expected type. For example: to use a select element
     * in a specification you can do:
     * 
     * <pre>
     *      &lt;select as="options" by="id:selectField"&gt;
     *      ...
     *      &lt;/select&gt;
     * </pre>
     * 
     * This construction allows convert select action which select items in a
     * verification of options in the specified select.
     */
    protected static final String ATT_AS = "as";

    /**
     * Creates a factory loading file 'sr_plugins_element.properties'.
     */
    public PluginFactoryElement() {
        super("sr_plugins_element.properties", PluginKind.ELEMENT);
    }

    @Override
    public IPlugin newPlugin(Node node, IContext context) throws PluginException {
        initialize();
        if (node instanceof Element) {
            Element ele = (Element) node;
            String name = ele.getQualifiedName().toLowerCase();
            int pos = name.indexOf(SEPARATOR);
            if (pos > 0) {
                ele.addAttribute(new Attribute("name", name.substring(pos + 1)));
                name = name.substring(0, pos);
                ele.setLocalName(name);
            }
            String as = ele.getAttributeValue(ATT_AS);
            if (as != null) {
                name = as;
            }
            IPlugin template = templates.get(name);
            if (template != null) {
                IPlugin create = UtilPlugin.create(context, template, ele);
                create.setParent(this);
                return create;
            } else {
                Class<? extends IPlugin> c = getClass(name);
                if (c != null) {
                    IPlugin create = UtilPlugin.create(context, c, ele);
                    create.setParent(this);
                    return create;
                }
            }
        }
        return PluginNop.emptyPlugin();
    }
}
