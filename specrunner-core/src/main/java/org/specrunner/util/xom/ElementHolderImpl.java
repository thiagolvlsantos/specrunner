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
import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.comparer.ComparatorException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;

/**
 * Default implementation of element holder.
 * 
 * @author Thiago Santos
 * 
 */
public class ElementHolderImpl implements IElementHolder {

    /**
     * The element encapsulated.
     */
    protected Element element;

    /**
     * Create a element holder.
     * 
     * @param element
     *            The element.
     */
    public ElementHolderImpl(Element element) {
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public boolean hasAttribute(String name) {
        return element.getAttribute(name) != null;
    }

    @Override
    public String getAttribute(String name) {
        return hasAttribute(name) ? element.getAttribute(name).getValue() : null;
    }

    @Override
    public void setAttribute(String name, String value) {
        if (hasAttribute(name)) {
            element.getAttribute(name).setValue(value);
        } else {
            element.addAttribute(new Attribute(name, value));
        }
    }

    @Override
    public void removeAttribute(String name) {
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute att = element.getAttribute(i);
            if (att.getQualifiedName().equalsIgnoreCase(name)) {
                element.removeAttribute(att);
                break;
            }
        }
    }

    @Override
    public String getValue() {
        return element.getValue();
    }

    @Override
    public IConverter getConverter() {
        return getConverter(SpecRunnerServices.get(IConverterManager.class).getDefault());
    }

    @Override
    public IConverter getConverter(IConverter converterDefault) {
        IConverter converter = null;
        if (hasAttribute("converter")) {
            String str = getAttribute("converter");
            IConverterManager cm = SpecRunnerServices.get(IConverterManager.class);
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
        for (int i = 0; i < element.getAttributeCount(); i++) {
            String arg = getAttribute("arg" + i);
            if (arg == null) {
                break;
            }
            params.add(arg);
        }
        return params.isEmpty() ? arguments : params;
    }

    @Override
    public IComparator getComparator() throws ComparatorException {
        return getComparator(SpecRunnerServices.get(IComparatorManager.class).getDefault());
    }

    @Override
    public IComparator getComparator(IComparator comparatorDefault) throws ComparatorException {
        IComparator comparator = null;
        if (hasAttribute("comparator")) {
            String str = getAttribute("comparator");
            IComparatorManager cm = SpecRunnerServices.get(IComparatorManager.class);
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
        converter = getConverter(converter);
        arguments = getArguments(arguments);
        Object value = null;
        if (hasAttribute("property")) {
            String str = getAttribute("property");
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
        } else {
            String tmp = getValue();
            if (hasAttribute("value")) {
                tmp = getAttribute("value");
            }
            value = UtilEvaluator.evaluate(tmp, context, silent);
        }
        Object[] args = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            args[i] = UtilEvaluator.evaluate(arguments.get(i), context, silent);
        }
        Object convert;
        try {
            convert = converter.convert(value, arguments.toArray());
        } catch (Exception e) {
            throw new PluginException(e);
        }
        return convert;
    }

    @Override
    public String toString() {
        return getElement() != null ? getElement().toXML() : "null";
    }
}
