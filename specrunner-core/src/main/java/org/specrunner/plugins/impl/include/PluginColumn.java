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
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;
import org.specrunner.util.xom.UtilNode;

/**
 * Plugin similar to SLIM Decision Table, or Fit Column Fixture.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginColumn extends AbstractPluginTable {

    /**
     * Bean name of the object instance created by this plugin. This name can be
     * used everywhere inside table.
     */
    public static final String BEAN_NAME = "$BEAN";

    /**
     * List of imported packages. Separated by ';'.
     */
    private String imports;
    /**
     * Local packages.
     */
    private List<String> localPackages = new LinkedList<String>();

    /**
     * The bean to be instanciated for use.
     */
    private String type;

    /**
     * Bean corresponding class.
     */
    private Class<?> typeClass;

    /**
     * Flag to pass content to assert methods.
     */
    private Boolean content = false;

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

    /**
     * Gets the bean information.
     * 
     * @return The bean information.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the bean information.
     * 
     * @param type
     *            The bean type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * If true, pass $CONTENT to assert method. In this case assert method
     * should have a method with one argument matching the expected result.
     * 
     * @return true, to send content, false, otherwise. Default is '
     *         <code>false</code>'.
     */
    public Boolean getContent() {
        return content;
    }

    /**
     * Set the content flag.
     * 
     * @param content
     *            The content.
     */
    public void setContent(Boolean content) {
        this.content = content;
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
        if (type != null) {
            try {
                typeClass = Class.forName(type);
            } catch (ClassNotFoundException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException("Bean class '" + type + "' not found.");
            }
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        context.saveStrict(UtilEvaluator.asVariable(BEAN_NAME), getObjectInstance(context, tableAdapter));

        List<RowAdapter> rows = tableAdapter.getRows();
        if (rows.isEmpty()) {
            throw new PluginException("Header information missing.");
        }
        List<String> methods = extractMethodNames(rows);
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            for (int j = 0; j < methods.size(); j++) {
                CellAdapter c = r.getCell(j);
                String method = methods.get(j);
                if (method.endsWith("?")) {
                    UtilNode.appendCss(c.getElement(), "eq");
                    c.setAttribute("value", BEAN_NAME + "." + method.substring(0, method.length() - 1) + "(" + (content ? "$CONTENT" : "") + ")");
                } else {
                    UtilNode.appendCss(c.getElement(), "execute");
                    c.setAttribute("value_", BEAN_NAME + "." + method + "($CONTENT)");
                }
            }
        }
        return ENext.DEEP;
    }

    /**
     * Get the object instance to be used by plugin actions.
     * 
     * @param context
     *            The context.
     * @param tableAdapter
     *            The adapter.
     * @return An object instance where all action will taken over.
     * @throws PluginException
     *             On creation/lookup errors.
     */
    protected Object getObjectInstance(IContext context, TableAdapter tableAdapter) throws PluginException {
        List<String> packages = new LinkedList<String>();
        packages.addAll(localPackages);
        packages.addAll(PluginImport.getPackages(context));

        Object instance = null;
        if (typeClass != null) {
            instance = newInstance(typeClass, tableAdapter);
        } else {
            List<CellAdapter> captions = tableAdapter.getCaptions();
            if (captions.size() > 1) {
                throw new PluginException("Table has '" + captions.size() + "' captions. Please use only one caption tag.");
            }
            if (captions.size() > 0) {
                String className = UtilString.camelCase(captions.get(0).getValue(), true);
                for (String pkg : packages) {
                    try {
                        Class<?> tmp = Class.forName(pkg + "." + className);
                        instance = newInstance(tmp, tableAdapter);
                        break;
                    } catch (ClassNotFoundException e) {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        if (instance == null) {
            instance = PluginHtml.getTestInstance();
            if (instance == null) {
                throw new PluginException("Type not specified with 'type' attribute, not found on packages '" + packages + "', or not executed by 'SRRunner'.");
            }
        }
        return instance;
    }

    /**
     * Create a instance of the object.
     * 
     * @param type
     *            The object type.
     * @param table
     *            The source table.
     * @return An instance of type.
     * @throws PluginException
     *             On creation errors.
     */
    protected Object newInstance(Class<?> type, TableAdapter table) throws PluginException {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create type instance.");
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create access type default constructor. Class:'" + type.getName() + "'");
        }
    }

    /**
     * Get the method names from this list.
     * 
     * @param rows
     *            The table rows.
     * @return The method names based on header line.
     */
    protected List<String> extractMethodNames(List<RowAdapter> rows) {
        RowAdapter header = rows.get(0);
        List<String> methods = new LinkedList<String>();
        for (CellAdapter h : header.getCells()) {
            String method = null;
            if (h.hasAttribute("method")) {
                method = h.getAttribute("method");
            } else {
                method = UtilString.camelCase(h.getValue());
            }
            methods.add(method);
        }
        return methods;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        // nothing.
    }
}
