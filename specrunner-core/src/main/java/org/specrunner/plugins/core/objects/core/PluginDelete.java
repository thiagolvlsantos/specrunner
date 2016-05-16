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
package org.specrunner.plugins.core.objects.core;

import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.core.objects.AbstractPluginObjectSelectUnique;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.util.xom.node.RowAdapter;

/**
 * Delete object from memory.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginDelete extends AbstractPluginObjectSelectUnique<IObjectManager> {

    /**
     * Create an update plugin.
     */
    public PluginDelete() {
        super(ObjectSelector.get());
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void perform(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception {
        Map<Class<?>, AbstractPluginObject> entities = source.getEntities();
        AbstractPluginObject plugin = entities.get(base.getClass());
        plugin.removeObject(plugin.makeKey(base));
        result.addResult(Success.INSTANCE, context.newBlock(row.getNode(), this));
    }
}
