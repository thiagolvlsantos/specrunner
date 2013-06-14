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

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Plugin similar to SLIM Decision Table, or Fit Column Fixture.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginColumn extends AbstractPluginTable {

    /**
     * List of imported packages. Separated by ';'.
     */
    private String imports;
    /**
     * Local packages.
     */
    private List<String> localPackages = new LinkedList<String>();

    private String bean;

    private Class<?> beanClass;

    /**
     * Get the import list.
     * 
     * @return The import list if any.
     */
    public String getImports() {
        return imports;
    }

    /**
     * Set the import list.
     * 
     * @param imports
     *            The imports.
     */
    public void setImports(String imports) {
        this.imports = imports;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context, TableAdapter table) throws PluginException {
        super.initialize(context, table);
        if (imports != null) {
            String[] pkgs = imports.split(";");
            for (String name : pkgs) {
                localPackages.add(name.trim());
            }
        }
        if (bean != null) {
            try {
                beanClass = Class.forName(bean);
            } catch (ClassNotFoundException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException("Bean class '" + bean + "' not found.");
            }
        }
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        List<String> packages = new LinkedList<String>();
        packages.addAll(localPackages);
        packages.addAll(PluginImport.getPackages(context));
        Class<?> type = null;
        Object instance = null;
        if (beanClass != null) {
            type = beanClass;
        } else {
            List<CellAdapter> captions = tableAdapter.getCaptions();
            if (captions.size() > 1) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Table has '" + captions.size() + "' captions. Please use only one caption tag."));
                return;
            }
            String className = UtilString.camelCase(captions.get(0).getValue(), true);
            for (String p : packages) {
                try {
                    type = Class.forName(p + "." + className);
                    break;
                } catch (ClassNotFoundException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        if (type == null) {
            Object testObject = PluginHtml.getTestInstance();
            if (testObject == null) {
                throw new PluginException("Type not specified with 'bean' attribute, or not found on packages '" + packages + "'.");
            } else {
                type = testObject.getClass();
                instance = testObject;
            }
        }
        System.out.println("CLASS NAME:" + type);
    }
}
