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
package org.specrunner.plugins.core.var;

import org.specrunner.context.IContext;
import org.specrunner.util.UtilLog;

/**
 * Defines a condition variable.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginDefineCondition extends AbstractPluginDefine {

    @Override
    public boolean isEval() {
        return false;
    }

    @Override
    protected boolean operation(Object obj, IContext context) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Bind_condition(" + getName() + ")->" + obj + "(" + (obj != null ? obj.getClass() : "null") + ")");
        }
        saveLocal(context, getName(), obj);
        return super.operation(obj, context);
    }
}
