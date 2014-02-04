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
package org.specrunner.plugins.impl.var;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginDual;
import org.specrunner.plugins.type.Command;
import org.specrunner.util.UtilEvaluator;

/**
 * Defines variables.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDefine extends AbstractPluginDual {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public boolean isEval() {
        return true;
    }

    @Override
    public String getName() {
        return UtilEvaluator.asVariable(super.getName());
    }

    @Override
    protected boolean operation(Object obj, IContext context) {
        Node node = context.getNode();
        if (node instanceof Element) {
            Element e = (Element) node;
            e.addAttribute(new Attribute("iname", super.getName()));
            e.addAttribute(new Attribute("instance", obj != null ? obj.getClass().getName() : "null"));
        }
        return true;
    }

    @Override
    protected Exception getError() {
        // useless exception
        return new PluginException();
    }
}
