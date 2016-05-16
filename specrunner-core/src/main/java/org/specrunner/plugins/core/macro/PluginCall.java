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
package org.specrunner.plugins.core.macro;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginNamed;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;

/**
 * A plugin which calls a macro by its name.
 * 
 * <p>
 * To use add class 'call' to the tag and specify the 'name' attribute with the
 * macro name you expect to perform.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCall extends AbstractPluginNamed {

    /**
     * CSS of called macros.
     */
    public static final String CSS_CALLED = "called";

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();

        // content processing
        UtilPlugin.performChildren(node, context, result);

        String name = getName();
        if (name == null) {
            setName(node.getValue());
        }
        String macroName = getName();
        Object obj = context.getByName(macroName);
        if (obj == null) {
            throw new PluginException("Macro named '" + macroName + "' not found.");
        }
        if (!(obj instanceof Node)) {
            throw new PluginException("Object with name '" + macroName + "' is not a macro.");
        }
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        Node macro = (Node) obj;
        for (int i = 0; i < macro.getChildCount(); i++) {
            parent.insertChild(macro.getChild(i).copy(), ++index);
        }

        Element ele = (Element) node;
        ele.addAttribute(new Attribute("name", macroName));
        ele.addAttribute(new Attribute("class", ele.getAttributeValue("class") + " " + CSS_CALLED));
        return ENext.DEEP;
    }
}
