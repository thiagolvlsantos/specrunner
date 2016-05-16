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

import java.util.LinkedList;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

/**
 * Maps a object but do not persist it. The default behavior of objects is put
 * them all in memory.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCollection extends AbstractPluginObject {

    protected static int serial = 0;
    protected List<Object> collection = new LinkedList<Object>();

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        collection.add(instance);
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        super.doEnd(context, result, table);
        String str = "$COLLECTION_" + (serial++);
        table.setAttribute("collection", str);
        context.saveGlobal(str, collection);
    }
}
