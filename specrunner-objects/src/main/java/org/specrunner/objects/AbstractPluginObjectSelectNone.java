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

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.util.impl.RowAdapter;

/**
 * Check if an object is not in object repository.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The source type.
 */
public abstract class AbstractPluginObjectSelectNone<T> extends AbstractPluginObjectSelect<T> {

    /**
     * Create a none plugin, provided an object selector.
     * 
     * @param selector
     *            The selector.
     */
    public AbstractPluginObjectSelectNone(IObjectSelector<T> selector) {
        super(selector);
    }

    @Override
    public void processList(IContext context, Object instance, RowAdapter row, IResultSet result, List<Object> list) throws Exception {
        if (list.isEmpty()) {
            for (int i = 0; i < row.getCellsCount(); i++) {
                result.addResult(Success.INSTANCE, context.newBlock(row.getCell(i).getElement(), this));
            }
        } else {
            Exception e = new PluginException("Element found in object repository. XML:" + row.getElement().toXML());
            for (int i = 0; i < row.getCellsCount(); i++) {
                result.addResult(i == 0 ? Failure.INSTANCE : Warning.INSTANCE, context.newBlock(row.getCell(i).getElement(), this), i == 0 ? e : null);
            }
        }
    }
}