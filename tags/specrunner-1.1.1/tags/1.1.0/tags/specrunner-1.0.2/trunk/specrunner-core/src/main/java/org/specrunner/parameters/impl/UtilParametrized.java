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
package org.specrunner.parameters.impl;

import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Helper class for setting parametrized objects.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilParametrized {

    /**
     * Hidden constructor.
     */
    private UtilParametrized() {
    }

    /**
     * Sets the properties of a given object based on attributes of a element.
     * 
     * @param context
     *            The context.
     * @param p
     *            The parameterized object whose attributes will be set based on
     *            element attributes.
     * @param element
     *            The reference element.
     * @throws PluginException
     *             On setting errors.
     */
    public static void setProperties(IContext context, IParametrized p, Element element) throws PluginException {
        if (p != null) {
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute n = element.getAttribute(i);
                String value = n.getValue();
                Object newValue = UtilEvaluator.evaluate(value, context);
                try {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("set(" + p + ")." + n.getQualifiedName() + "=" + newValue);
                    }
                    String name = n.getQualifiedName();
                    p.setParameter(name, newValue);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Sets the properties of a given object based on attributes of a element.
     * 
     * @param context
     *            The context.
     * @param p
     *            The parameterized object whose attributes will be set based on
     *            element attributes.
     * @param parameters
     *            Map of parameters.
     * @throws PluginException
     *             On setting errors.
     */
    public static void setProperties(IContext context, IParametrized p, Map<String, Object> parameters) throws PluginException {
        if (p != null) {
            for (Entry<String, Object> e : parameters.entrySet()) {
                try {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("set(" + p + ")." + e.getKey() + "=" + e.getValue());
                    }
                    p.setParameter(e.getKey(), e.getValue());
                } catch (Exception ex) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(ex.getMessage(), ex);
                    }
                }
            }
        }
    }
}