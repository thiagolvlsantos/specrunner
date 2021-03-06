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
package org.specrunner.plugins.core.logical;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginDual;
import org.specrunner.plugins.type.Assertion;

/**
 * Perform a 'false' assertion.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFalse extends AbstractPluginDual {

    private Object obj;

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected boolean operation(Object obj, IContext context) throws PluginException {
        this.obj = obj;
        return obj instanceof Boolean && !(Boolean) obj;
    }

    @Override
    protected Throwable getError() {
        return new PluginException("Expected a boolean 'false', received: '" + obj + "' of type " + (obj != null ? obj.getClass() : "null"));
    }
}