/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.util.functions.core;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.util.UtilLog;
import org.specrunner.util.functions.IPredicate;

/**
 * Defines a predicate for object properties.
 * 
 * @author Thiago Santos.
 * 
 */
public class PredicateProperty<P> implements IPredicate<P> {

    private String property;
    private Object value;

    public PredicateProperty(String property, Object value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public Boolean apply(P p) {
        try {
            Object tmp = PropertyUtils.getProperty(p, property);
            return tmp == null ? value == null : tmp.equals(value);
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        } catch (InvocationTargetException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        } catch (NoSuchMethodException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        return Boolean.FALSE;
    }
}
