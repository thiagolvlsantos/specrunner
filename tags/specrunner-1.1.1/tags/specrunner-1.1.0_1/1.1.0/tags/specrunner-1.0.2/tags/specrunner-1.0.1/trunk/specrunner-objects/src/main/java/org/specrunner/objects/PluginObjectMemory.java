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
package org.specrunner.objects;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.util.impl.RowAdapter;

/**
 * Maps a object but do not persist it. The default behavior of objects, put
 * them all in memory.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginObjectMemory extends AbstractPluginObject {

    @Override
    protected boolean isMapped() {
        return true;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        // nothing: memory mapping is default.
    }
}
