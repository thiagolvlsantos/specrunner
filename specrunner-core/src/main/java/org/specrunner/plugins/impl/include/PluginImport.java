/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.plugins.impl.include;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Add package information as SLIM import tables.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginImport extends AbstractPluginTable {

    /**
     * The import list name.
     */
    public static final String PACKAGES_NAME = UtilEvaluator.asVariable("packages");

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        List<String> list = getPackages(context);
        List<RowAdapter> rows = tableAdapter.getRows();
        for (RowAdapter r : rows) {
            if (r.getCellsCount() == 0) {
                result.addResult(Failure.INSTANCE, context.newBlock(r.getElement(), this), new PluginException("Package name missing. The line is empty."));
                continue;
            }
            CellAdapter cell = r.getCell(0);
            String value = cell.getValue();
            Element element = cell.getElement();
            if (value == null) {
                result.addResult(Failure.INSTANCE, context.newBlock(element, this), new PluginException("Package '" + value + "' not found on classpath."));
            } else {
                list.add(value);
                result.addResult(Success.INSTANCE, context.newBlock(element, this));
            }
        }
    }

    /**
     * Recover the package list in context, if any. Empty list if not found.
     * 
     * @param context
     *            Context.
     * @return The package list, if exists, false, otherwise.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getPackages(IContext context) {
        List<String> list = (List<String>) context.getByName(PACKAGES_NAME);
        if (list == null) {
            list = new LinkedList<String>();
            context.saveGlobal(PACKAGES_NAME, list);
        }
        return list;
    }

}