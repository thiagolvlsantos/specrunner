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
package org.specrunner.util.xom.node.core;

import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.IComparatorManager;
import org.specrunner.context.IContext;
import org.specrunner.converters.IConverter;
import org.specrunner.converters.IConverterManager;
import org.specrunner.features.IFeatureManager;
import org.specrunner.formatters.FormatterException;
import org.specrunner.formatters.IFormatter;
import org.specrunner.formatters.IFormatterManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.readers.IReader;
import org.specrunner.readers.IReaderManager;
import org.specrunner.readers.ReaderException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.expression.UtilExpression;
import org.specrunner.util.xom.node.INodeHolder;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

/**
 * Default implementation of element holder.
 * 
 * @author Thiago Santos
 * 
 */
public class NodeHolderDefault implements INodeHolder {

    /**
     * The node encapsulated.
     */
    protected Node node;

    /**
     * The attribute value.
     */
    protected String attributeValue = ATTRIBUTE_VALUE;

    /**
     * Create a element holder.
     * 
     * @param element
     *            The element.
     */
    public NodeHolderDefault(Node element) {
        this.node = element;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void setNode(Node element) {
        this.node = element;
    }

    @Override
    public void detach() {
        if (node != null) {
            node.detach();
        }
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String getQualifiedName() {
        if (node instanceof Element) {
            return ((Element) node).getQualifiedName();
        }
        return null;
    }

    @Override
    public boolean hasName(String name) {
        String tmp = getQualifiedName();
        return tmp != null && tmp.equalsIgnoreCase(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return node instanceof Element && ((Element) node).getAttribute(name) != null;
    }

    @Override
    public String getAttribute(String name) {
        return hasAttribute(name) ? ((Element) node).getAttribute(name).getValue() : null;
    }

    @Override
    public String getAttribute(String name, String defaultValue) {
        return hasAttribute(name) ? ((Element) node).getAttribute(name).getValue() : defaultValue;
    }

    @Override
    public void setAttribute(String name, String value) {
        if (hasAttribute(name)) {
            ((Element) node).getAttribute(name).setValue(value);
        } else {
            if (node instanceof Element) {
                ((Element) node).addAttribute(new Attribute(name, value));
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (node instanceof Element) {
            Element element = (Element) node;
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute att = element.getAttribute(i);
                if (att.getQualifiedName().equalsIgnoreCase(name)) {
                    element.removeAttribute(att);
                    break;
                }
            }
        }
    }

    @Override
    public boolean attributeContains(String name, String value) {
        return hasAttribute(name) && getAttribute(name).contains(value);
    }

    @Override
    public boolean attributeEquals(String name, String value) {
        return hasAttribute(name) && getAttribute(name).equalsIgnoreCase(value);
    }

    @Override
    public String getValue(IContext context) {
        return getValue(context, null);
    }

    @Override
    public String getValue(IContext context, Object[] args) {
        try {
            return getReader().read(context, node, args);
        } catch (ReaderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setValue(String text) {
        if (node instanceof Text) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.removeChild(index);
            parent.insertChild(new Text(text), index);
        } else if (node instanceof Element) {
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                node.getChild(i).detach();
            }
            ((Element) node).appendChild(text);
        }
    }

    @Override
    public void prepend(String text) {
        if (node instanceof Text) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.insertChild(new Text(text), Math.max(0, index - 1));
        } else if (node instanceof Element) {
            ((Element) node).insertChild(text, 0);
        }
    }

    @Override
    public void prepend(Node child) {
        if (node instanceof Text) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.insertChild(child, Math.max(0, index - 1));
        } else if (node instanceof Element) {
            ((Element) node).insertChild(child, 0);
        }
    }

    @Override
    public void append(String text) {
        if (node instanceof Text) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.insertChild(new Text(text), index + 1);
        } else if (node instanceof Element) {
            ((Element) node).appendChild(text);
        }
    }

    @Override
    public void append(Node child) {
        if (node instanceof Text) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.insertChild(child, index + 1);
        } else if (node instanceof Element) {
            ((Element) node).appendChild(child);
        }
    }

    @Override
    public IReader getReader() {
        return getReader(SRServices.getReaderManager().getDefault());
    }

    @Override
    public IReader getReader(IReader readerDefault) {
        IReader reader = null;
        if (hasAttribute(ATTRIBUTE_READER)) {
            String str = getAttribute(ATTRIBUTE_READER);
            IReaderManager cm = SRServices.getReaderManager();
            reader = cm.get(str);
            if (reader == null) {
                try {
                    reader = (IReader) Class.forName(str).newInstance();
                    cm.bind(str, reader);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        if (reader == null) {
            reader = readerDefault;
        }
        return reader;
    }

    @Override
    public IConverter getConverter() {
        return getConverter(SRServices.getConverterManager().getDefault());
    }

    @Override
    public IConverter getConverter(IConverter converterDefault) {
        IConverter converter = null;
        if (hasAttribute(ATTRIBUTE_CONVERTER)) {
            String str = getAttribute(ATTRIBUTE_CONVERTER);
            IConverterManager cm = SRServices.getConverterManager();
            converter = cm.get(str);
            if (converter == null) {
                try {
                    converter = (IConverter) Class.forName(str).newInstance();
                    cm.bind(str, converter);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        if (converter == null) {
            converter = converterDefault;
        }
        return converter;
    }

    @Override
    public List<String> getArguments() {
        return getArguments(new LinkedList<String>());
    }

    @Override
    public List<String> getArguments(List<String> arguments) {
        List<String> params = new LinkedList<String>();
        if (node instanceof Element) {
            Element element = (Element) node;
            for (int i = 0; i < element.getAttributeCount(); i++) {
                String arg = getAttribute(ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i);
                if (arg == null) {
                    break;
                }
                params.add(arg);
            }
        }
        return params.isEmpty() ? arguments : params;
    }

    @Override
    public IComparator getComparator() throws ComparatorException {
        return getComparator(SRServices.getComparatorManager().getDefault());
    }

    @Override
    public IComparator getComparator(IComparator comparatorDefault) throws ComparatorException {
        IComparator comparator = null;
        if (hasAttribute(ATTRIBUTE_COMPARATOR)) {
            String str = getAttribute(ATTRIBUTE_COMPARATOR);
            IComparatorManager cm = SRServices.getComparatorManager();
            comparator = cm.get(str);
            if (comparator == null) {
                try {
                    comparator = (IComparator) Class.forName(str).newInstance();
                    cm.bind(str, comparator);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                    if (comparatorDefault == null) {
                        throw new ComparatorException(e);
                    }
                }
            }
        }
        if (comparator == null) {
            comparator = comparatorDefault;
        }
        return comparator;
    }

    @Override
    public List<String> getFormatterArguments() {
        return getFormatterArguments(new LinkedList<String>());
    }

    @Override
    public List<String> getFormatterArguments(List<String> arguments) {
        List<String> params = new LinkedList<String>();
        if (node instanceof Element) {
            Element element = (Element) node;
            for (int i = 0; i < element.getAttributeCount(); i++) {
                String arg = getAttribute(ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + i);
                if (arg == null) {
                    break;
                }
                params.add(arg);
            }
        }
        return params.isEmpty() ? arguments : params;
    }

    @Override
    public IFormatter getFormatter() throws FormatterException {
        return getFormatter(null);
    }

    @Override
    public IFormatter getFormatter(IFormatter formatterDefault) throws FormatterException {
        IFormatter formatter = null;
        if (hasAttribute(ATTRIBUTE_FORMATTER)) {
            String str = getAttribute(ATTRIBUTE_FORMATTER);
            IFormatterManager cm = SRServices.getFormatterManager();
            formatter = cm.get(str);
            if (formatter == null) {
                try {
                    formatter = (IFormatter) Class.forName(str).newInstance();
                    cm.bind(str, formatter);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                    if (formatterDefault == null) {
                        throw new FormatterException(e);
                    }
                }
            }
        }
        if (formatter == null) {
            formatter = formatterDefault;
        }
        return formatter;
    }

    @Override
    public Object getObject(IContext context, boolean silent) throws PluginException {
        return getObject(context, silent, getConverter(), getArguments());
    }

    @Override
    public Object getObject(IContext context, boolean silent, IConverter converter, List<String> arguments) throws PluginException {
        Object local = getLocal(context, silent, converter, arguments);
        try {
            IFormatter formatter = getFormatter();
            if (formatter != null) {
                return formatter.format(local, getFormatterArguments().toArray());
            }
        } catch (FormatterException e) {
            throw new PluginException(e);
        }
        return local;
    }

    /**
     * @see getObject.
     */
    protected Object getLocal(IContext context, boolean silent, IConverter converter, List<String> arguments) throws PluginException {
        boolean extend = context.getNode() != node;
        if (extend) {
            context.push(context.newBlock(node, null));
            context.addMetadata();
        }
        try {
            Object value = null;
            if (isProperty()) {
                value = getProperty(context, silent);
            } else {
                value = getValue(context, silent);
                IConverter converterLocal = getConverter(converter);
                List<Object> argumentsLocal = getArgumentsLocal(context, silent, arguments);
                try {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Trying to convert '" + value + "' of type " + (value != null ? value.getClass() : " null") + " using " + converterLocal + " with arguments: " + argumentsLocal);
                    }
                    value = converterLocal.convert(value, argumentsLocal.toArray());
                } catch (Exception e) {
                    throw new PluginException("Error on converter: " + converterLocal + ". ERROR:" + e.getMessage(), e);
                }
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Converted value is '" + value + "' of type " + (value != null ? value.getClass() : " null"));
                }
            }
            return value;
        } finally {
            if (extend) {
                context.pop();
            }
        }
    }

    /**
     * Is property value set.
     * 
     * @return true, if 'property' is set, and 'forceValue' is not true.
     */
    protected boolean isProperty() {
        return hasAttribute(ATTRIBUTE_PROPERTY) && !attributeEquals(ATTRIBUTE_FORCE_VALUE, "true");
    }

    /**
     * Get property value.
     * 
     * @param context
     *            A context.
     * @param silent
     *            Silent evaluation flag.
     * @return The property value (including null), or null if property path has
     *         some null.
     * @throws PluginException
     *             On lookup errors.
     */
    protected Object getProperty(IContext context, boolean silent) throws PluginException {
        Object property = null;
        String str = getAttribute(ATTRIBUTE_PROPERTY);
        int pos = str.indexOf('.');
        if (pos <= 0) {
            throw new PluginException("Bean name or property missing in property='" + str + "'.");
        }
        String head = str.substring(0, pos);
        Object bean = UtilExpression.evaluate(head, context, silent);
        IFeatureManager fm = SRServices.getFeatureManager();
        Boolean acceptNullPath = (Boolean) fm.get(FEATURE_PROPERTY_ACCEPT_NULL_PATH, DEFAULT_PROPERTY_ACCEPT_NULL_PATH);
        Boolean invalidPathAsNull = (Boolean) fm.get(FEATURE_PROPERTY_INVALID_PATH_AS_NULL, DEFAULT_PROPERTY_INVALID_PATH_AS_NULL);
        try {
            StringTokenizer st = new StringTokenizer(str.substring(pos + 1), ".");
            StringBuilder path = new StringBuilder(head);
            while (bean != null && st.hasMoreTokens()) {
                String part = st.nextToken();
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, part);
                if ((pd == null || pd.getReadMethod() == null) && invalidPathAsNull) {
                    bean = null;
                    break;
                }
                bean = PropertyUtils.getProperty(bean, part);
                path.append('.');
                path.append(part);
                if (bean == null && !acceptNullPath && st.hasMoreElements()) {
                    throw new PluginException("Invalid null value for part '" + path + "' of property '" + str + "'.");
                }
            }
            property = bean;
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e);
        }
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("Bean property (" + str + ") value is '" + property + "' of type " + (property != null ? property.getClass() : " null"));
        }
        return property;
    }

    /**
     * Get value.
     * 
     * @param context
     *            A context.
     * @param silent
     *            Silent evaluation flag.
     * @return The tag value, evaluated (default is evaluate) or not (set
     *         eval='false').
     * @throws PluginException
     *             On evaluation errors.
     */
    protected Object getValue(IContext context, boolean silent) throws PluginException {
        String str;
        Object value;
        if (hasAttribute(attributeValue)) {
            boolean forceContent = hasAttribute(ATTRIBUTE_FORCE_CONTENT);
            if (forceContent) {
                str = getValue(context);
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Forced content, value is '" + str + "'.");
                }
                value = notCeval() ? str : UtilExpression.evaluate(str, context, silent);
            } else {
                str = getAttribute(attributeValue);
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Attribute value present, value is '" + str + "'.");
                }
                value = notEval() ? str : UtilExpression.evaluate(str, context, silent);
            }
        } else {
            str = getValue(context);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("Content value is '" + str + "'.");
            }
            value = notEval() ? str : UtilExpression.evaluate(str, context, silent);
        }
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("Evaluated value is '" + value + "' of type " + (value != null ? value.getClass() : "null"));
        }
        return value;
    }

    /**
     * Is evaluation turned off. Default is false.
     * 
     * @return true, if evaluation flag set to 'false', false otherwise.
     */
    protected boolean notEval() {
        return attributeEquals(ATTRIBUTE_EVALUATION, Boolean.FALSE.toString());
    }

    /**
     * Is content evaluation turned off. Default is false.
     * 
     * @return true, if content evaluation flag set to 'false', false otherwise.
     */
    protected boolean notCeval() {
        return attributeEquals(ATTRIBUTE_CEVALUATION, Boolean.FALSE.toString());
    }

    /**
     * Get arguments for evaluation.
     * 
     * @param context
     *            A context.
     * @param silent
     *            Silent evaluation flag.
     * @param arguments
     *            Value arguments.
     * @return List of arguments.
     * @throws PluginException
     *             On evaluation errors.
     */
    protected List<Object> getArgumentsLocal(IContext context, boolean silent, List<String> arguments) throws PluginException {
        List<Object> result = new LinkedList<Object>();
        IFeatureManager fm = SRServices.getFeatureManager();
        Boolean eval = (Boolean) fm.get(FEATURE_EVAL_ARGS, DEFAULT_EVAL_ARGS);
        List<String> local = getArguments(arguments);
        if (eval) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("Eval args: " + local);
            }
            for (int i = 0; i < local.size(); i++) {
                result.add(UtilExpression.evaluate(local.get(i), context, silent));
            }
        } else {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("Not eval args: " + local);
            }
            result.addAll(local);
        }
        return result;
    }

    @Override
    public String toXML() {
        return getNode() != null ? getNode().toXML() : "null";
    }

    @Override
    public String toString() {
        return toXML();
    }
}
