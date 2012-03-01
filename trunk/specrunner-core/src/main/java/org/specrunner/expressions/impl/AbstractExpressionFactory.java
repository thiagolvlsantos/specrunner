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
package org.specrunner.expressions.impl;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.expressions.IExpressionFactory;

/**
 * Partial expression factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractExpressionFactory implements IExpressionFactory {
    private Map<String, Object> predefinedValues = new HashMap<String, Object>();
    private Map<String, Class<?>> predefinedClasses = new HashMap<String, Class<?>>();

    @Override
    public void clearPredefinedValues() {
        predefinedValues.clear();
    }

    @Override
    public void removePredefinedValue(String name) {
        predefinedValues.remove(name);
    }

    @Override
    public void bindPredefinedValue(String name, Object value) {
        predefinedValues.put(name, value);
    }

    @Override
    public Map<String, Object> getPredefinedValues() {
        return predefinedValues;
    }

    @Override
    public void clearPredefinedClasses() {
        predefinedClasses.clear();
    }

    @Override
    public void removePredefinedClass(String name) {
        predefinedClasses.remove(name);
    }

    @Override
    public void bindPredefinedClass(String name, Class<?> clazz) {
        predefinedClasses.put(name, clazz);
    }

    @Override
    public Map<String, Class<?>> getPredefinedClasses() {
        return predefinedClasses;
    }
}