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
package org.specrunner.plugins.core.var;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.expressions.Unsilent;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.node.INodeHolderFactory;

import nu.xom.Node;

/**
 * Perform a execution. If execute returns something bind it to a local
 * variable.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginExecute extends AbstractPluginDefine {

    private boolean set = false;

    @Override
    @Unsilent
    public void setValue(Object value) {
        super.setValue(value);
        set = true;
    }

    protected Object getObjectValue(IContext context, Node node) throws PluginException {
        if (set) {
            return getValue();
        }
        return SRServices.get(INodeHolderFactory.class).newHolder(node).getObject(context, true);
    }

    @Override
    protected boolean operation(Object obj, IContext context) {
        String n = getName();
        if (n != null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Bind_execute(" + n + ")->" + obj + "(" + (obj != null ? obj.getClass() : "null") + ")");
            }
            saveLocal(context, n, obj);
        }
        return super.operation(obj, context);
    }
}
