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
package org.specrunner.util.xom;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.IComparatorManager;
import org.specrunner.context.IContext;
import org.specrunner.converters.IConverter;
import org.specrunner.converters.IConverterManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Default implementation of element holder.
 * 
 * @author Thiago Santos
 * 
 */
public class NodeHolder implements INodeHolder {

    /**
     * The node encapsulated.
     */
    protected Node node;

    /**
     * Create a element holder.
     * 
     * @param element
     *            The element.
     */
    public NodeHolder(Node element) {
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
    public String getValue() {
        return node.getValue();
    }

    @Override
    public IConverter getConverter() {
        return getConverter(SpecRunnerServices.getConverterManager().getDefault());
    }

    @Override
    public IConverter getConverter(IConverter converterDefault) {
        IConverter converter = null;
        if (hasAttribute(ATTRIBUTE_CONVERTER)) {
            String str = getAttribute(ATTRIBUTE_CONVERTER);
            IConverterManager cm = SpecRunnerServices.getConverterManager();
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
                String arg = getAttribute(ATTRIBUTE_ARGUMENT_PREFIX + i);
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
        return getComparator(SpecRunnerServices.getComparatorManager().getDefault());
    }

    @Override
    public IComparator getComparator(IComparator comparatorDefault) throws ComparatorException {
        IComparator comparator = null;
        if (hasAttribute(ATTRIBUTE_COMPARATOR)) {
            String str = getAttribute(ATTRIBUTE_COMPARATOR);
            IComparatorManager cm = SpecRunnerServices.getComparatorManager();
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
    public Object getObject(IContext context, boolean silent) throws PluginException {
        return getObject(context, silent, getConverter(), getArguments());
    }

    @Override
    public Object getObject(IContext context, boolean silent, IConverter converter, List<String> arguments) throws PluginException {
        boolean extend = context.getNode() != node;
        if (extend) {
            context.push(context.newBlock(node, null));
            context.addMetadata();
        }
        try {
            converter = getConverter(converter);
            arguments = getArguments(arguments);
            Object value = null;
            if (attributeEquals(ATTRIBUTE_EVALUATION, Boolean.FALSE.toString())) {
                value = getValue();
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Evaluation ignored, value is '" + value + "' of type " + (value != null ? value.getClass() : " null"));
                }
            } else {
                if (hasAttribute(ATTRIBUTE_PROPERTY)) {
                    String str = getAttribute(ATTRIBUTE_PROPERTY);
                    int pos = str.indexOf('.');
                    if (pos <= 0) {
                        throw new PluginException("Bean name or property missing in property='" + str + "'.");
                    }
                    Object bean = UtilEvaluator.evaluate(str.substring(0, pos), context, silent);
                    try {
                        value = PropertyUtils.getProperty(bean, str.substring(pos + 1));
                    } catch (IllegalAccessException e) {
                        throw new PluginException(e);
                    } catch (InvocationTargetException e) {
                        throw new PluginException(e);
                    } catch (NoSuchMethodException e) {
                        throw new PluginException(e);
                    }
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Bean property (" + str + ") value is '" + value + "' of type " + (value != null ? value.getClass() : " null"));
                    }
                } else {
                    String tmp;
                    if (hasAttribute(ATTRIBUTE_VALUE)) {
                        tmp = getAttribute(ATTRIBUTE_VALUE);
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace("Attribute value present, value is '" + tmp + "'.");
                        }
                    } else {
                        tmp = getValue();
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace("Content value is '" + tmp + "'.");
                        }
                    }
                    value = UtilEvaluator.evaluate(tmp, context, silent);
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Evaluated value is '" + value + "' of type " + (value != null ? value.getClass() : " null"));
                    }
                }
                Object[] args = new Object[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    args[i] = UtilEvaluator.evaluate(arguments.get(i), context, silent);
                }
                Object convert;
                try {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Trying to convert '" + value + "' of type " + (value != null ? value.getClass() : " null") + " using " + converter + " with arguments: " + Arrays.toString(args));
                    }
                    convert = converter.convert(value, arguments.toArray());
                } catch (Exception e) {
                    throw new PluginException(e);
                }
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Converted value is '" + value + "' of type " + (value != null ? value.getClass() : " null"));
                }
                return convert;
            }
            return value;
        } finally {
            if (extend) {
                context.pop();
            }
        }
    }

    @Override
    public String toString() {
        return getNode() != null ? getNode().toXML() : "null";
    }
}
