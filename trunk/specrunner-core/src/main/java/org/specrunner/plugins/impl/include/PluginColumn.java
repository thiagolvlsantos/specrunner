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

import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.plugins.impl.PluginAssertion;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.impl.var.PluginBean;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * This plugin stand for a SLIM/Fit Column like plugin implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginColumn extends AbstractPluginTable {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        Object instance = getObjectInstance(context, tableAdapter);
        context.saveStrict(UtilEvaluator.asVariable(PluginBean.BEAN_NAME), instance);

        List<RowAdapter> rows = tableAdapter.getRows();
        if (rows.isEmpty()) {
            throw new PluginException("Header information missing.");
        }
        RowAdapter header = rows.get(0);
        List<String> features = new LinkedList<String>();
        List<IConverter> converters = new LinkedList<IConverter>();
        List<List<String>> args = new LinkedList<List<String>>();
        try {
            extractFeatures(context, header, features, converters, args);
        } catch (ConverterException e) {
            result.addResult(Failure.INSTANCE, context.peek(), e);
            return ENext.DEEP;
        }
        IAccessFactory accessFactory = SpecRunnerServices.get(IAccessFactory.class);
        List<IAccess> accesses = new LinkedList<IAccess>();
        for (String f : features) {
            accesses.add(accessFactory.newAccess(instance, f.replace("?", "")));
        }
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (r.getCellsCount() != features.size()) {
                result.addResult(Failure.INSTANCE, context.peek(), "Number of coluns in line " + i + " is different of headers (" + features.size() + ").");
                continue;
            }
            for (int j = 0; j < features.size(); j++) {
                CellAdapter c = r.getCell(j);
                Object value;
                try {
                    value = c.getObject(context, true, converters.get(j), args.get(j));
                } catch (PluginException e) {
                    result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Invalid value for '" + c + "'.", e));
                    continue;
                }
                String feature = features.get(j);
                IAccess access = accesses.get(j);
                if (access == null) {
                    result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Invalid access information. Not found public attribute, bean property or method named '" + feature.replace("?", "") + "' in object '" + instance.getClass() + "'."));
                    continue;
                }
                if (feature.endsWith("?")) {
                    Object received = null;
                    CellAdapter hd = header.getCells().get(j);
                    String content = "content";
                    if ((hd.hasAttribute(content) && Boolean.parseBoolean(hd.getAttribute(content))) || (c.hasAttribute(content) && Boolean.parseBoolean(c.getAttribute(content)))) {
                        try {
                            received = access.get(instance, feature, value);
                        } catch (Exception e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                            result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Could not get " + feature + "(" + value + ") in '" + (instance != null ? instance.getClass() : "null") + "'.", e));
                        }
                    } else {
                        try {
                            received = access.get(instance, feature);
                        } catch (Exception e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                            result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Could not get " + feature + " in '" + (instance != null ? instance.getClass() : "null") + "'.", e));
                        }
                    }
                    try {
                        UtilPlugin.compare(c.getNode(), PluginAssertion.INSTANCE, result, c.getComparator(SpecRunnerServices.getComparatorManager().get("string")), value, received);
                    } catch (ComparatorException e) {
                        result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Could not find comparator in " + c.toString() + ".", e));
                    }
                } else {
                    try {
                        access.set(instance, feature, value);
                        result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                    } catch (Exception e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new PluginException("Could not set value '" + value + "' of type " + (value != null ? value.getClass() : "undefined") + " to " + feature + " in '" + (instance != null ? instance.getClass() : "null") + "'.", e));
                    }
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
     * @return An object instance where all actions will be taken over.
     * @throws PluginException
     *             On creation/lookup errors.
     */
    protected Object getObjectInstance(IContext context, TableAdapter tableAdapter) throws PluginException {
        Object instance = null;
        List<CellAdapter> captions = tableAdapter.getCaptions();
        if (captions.size() > 1) {
            throw new PluginException("Table has '" + captions.size() + "' captions. Please use only one caption tag.");
        }
        if (captions.size() > 0) {
            // captions must use imports to set packages.
            String className = UtilString.camelCase(captions.get(0).getValue(), true);
            for (String pkg : PluginImport.getPackages(context)) {
                try {
                    instance = newInstance(Class.forName(pkg + "." + className), tableAdapter);
                    break;
                } catch (ClassNotFoundException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        if (instance == null) {
            // by bean, use the closest bean instance
            instance = PluginBean.getBean(context);
        }
        if (instance == null) {
            // default is consider page as bean.
            instance = PluginHtml.getTestInstance();
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
            throw new PluginException("Could not create type instance.", e);
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create access type default constructor. Class:'" + type.getName() + "'", e);
        }
    }

    /**
     * Get the feature names from this list.
     * 
     * @param context
     *            The test context.
     * @param header
     *            The table rows.
     * @param features
     *            The feature list.
     * @param converters
     *            The converter list.
     * @param args
     *            The arguments. The arguments list.
     * @throws ConverterException
     *             On converter lookup errors.
     * @throws PluginException
     *             On feature extraction errors.
     */
    protected void extractFeatures(IContext context, RowAdapter header, List<String> features, List<IConverter> converters, List<List<String>> args) throws ConverterException, PluginException {
        for (CellAdapter h : header.getCells()) {
            features.add(feature(h));
            converters.add(h.getConverter());
            args.add(h.getArguments());
        }
    }

    /**
     * Get a feature name.
     * 
     * @param h
     *            The element.
     * @return The name.
     */
    protected String feature(CellAdapter h) {
        String value = h.getValue();
        String feature;
        if (h.hasAttribute("feature")) {
            feature = UtilString.camelCase(h.getAttribute("feature"));
        } else {
            feature = UtilString.camelCase(value);
        }
        if (value != null && value.trim().endsWith("?")) {
            feature = feature + "?";
        }
        return feature;
    }
}
