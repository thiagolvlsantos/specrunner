/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.plugins.core.include;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;
import org.specrunner.util.xom.node.UtilTable;

import nu.xom.Node;

/**
 * Add package information as SLIM import tables. Imports can also be performed
 * simply by adding attribute 'imports' to any element.
 * 
 * <p>
 * Imports can be performed:
 * <ul>
 * <li>in blocks, i.e.
 * 
 * <pre>
 * &lt;.. imports="package1;package2;...;packageN"/&gt;
 * </pre>
 * 
 * on every valid tag;</li>
 * <li>in tables, i.e.
 * 
 * In columns
 * 
 * <pre>
 *          &lt;table class="imports"&gt
 *              &lt;tr&gt;&lt;td&gt;package1&lt;/td&gt;&lt;td&gt;...&lt;/td&gt;&lt;td&gt;packageN&lt;/td&gt;&lt;/tr&gt;
 *          &lt;/table&gt;
 * </pre>
 * 
 * or in rows
 * 
 * <pre>
 *          &lt;table class="imports"&gt
 *              &lt;tr&gt;&lt;td&gt;package1&lt;/td&gt;&lt;/tr&gt;
 *              &lt;tr&gt;&lt;td&gt;...&lt;/td&gt;&lt;/tr&gt;
 *              &lt;tr&gt;&lt;td&gt;packageN&lt;/td&gt;&lt;/tr&gt;
 *          &lt;/table&gt;
 * </pre>
 * 
 * </li>
 * 
 * <li>in free tags, i.e.
 * 
 * <pre>
 *      &lt;imports&gt;package1;...;packageN&lt;imports&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * 
 * 
 * @author Thiago Santos
 * 
 */
public class PluginImport extends AbstractPluginValue {

    /**
     * The import list name.
     */
    public static final String PACKAGES_NAME = "$PACKAGES";

    /**
     * Set a import package names separated by ';'.
     */
    protected String imports;

    /**
     * The packages set in imports.
     */
    protected String[] packages;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    /**
     * Get the list of imports.
     * 
     * @return The import list.
     */
    public String getImports() {
        return imports;
    }

    /**
     * Set the list of imports.
     * 
     * @param imports
     *            The import list separated by ';'.
     */
    public void setImports(String imports) {
        this.imports = imports;
        if (imports != null) {
            packages = imports.split(";");
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        List<String> list = getPackages(context);
        if (UtilTable.isTable(node)) {
            TableAdapter tableAdapter = UtilTable.newTable(node);
            List<RowAdapter> rows = tableAdapter.getRows();
            for (RowAdapter r : rows) {
                for (CellAdapter c : r.getCells()) {
                    list.add(c.getValue(context));
                    result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                }
            }
        } else {
            if (packages == null) {
                setImports(node.getValue());
            }
            if (packages != null) {
                for (String s : packages) {
                    list.add(s);
                }
                result.addResult(Success.INSTANCE, context.newBlock(node, this));
            }
        }
        return ENext.DEEP;
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
