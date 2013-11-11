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
package org.specrunner.expressions.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.context.IModel;
import org.specrunner.expressions.IExpressionFactory;

/**
 * Partial expression factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractExpressionFactory implements IExpressionFactory {
    /**
     * The set of predefined values.
     */
    private Map<String, Object> predefinedValues = new HashMap<String, Object>();
    /**
     * The set of predefined classes.
     */
    private Map<String, Class<?>> predefinedClasses = new HashMap<String, Class<?>>();
    /**
     * The set of predefined models.
     */
    private Map<String, IModel<?>> predefinedModels = new HashMap<String, IModel<?>>();

    @Override
    public IExpressionFactory clearValues() {
        predefinedValues.clear();
        return this;
    }

    @Override
    public IExpressionFactory removeValue(String name) {
        predefinedValues.remove(name);
        return this;
    }

    @Override
    public IExpressionFactory bindValue(String name, Object value) {
        predefinedValues.put(name, value);
        return this;
    }

    @Override
    public IExpressionFactory setValues(Map<String, Object> predefinedValues) {
        this.predefinedValues = predefinedValues;
        return this;
    }

    @Override
    public Map<String, Object> getValues() {
        return predefinedValues;
    }

    @Override
    public IExpressionFactory clearClasses() {
        predefinedClasses.clear();
        return this;
    }

    @Override
    public IExpressionFactory removeClass(String name) {
        predefinedClasses.remove(name);
        return this;
    }

    @Override
    public IExpressionFactory bindClass(String name, Class<?> clazz) {
        predefinedClasses.put(name, clazz);
        return this;
    }

    @Override
    public IExpressionFactory setClasses(Map<String, Class<?>> predefinedClasses) {
        this.predefinedClasses = predefinedClasses;
        return this;
    }

    @Override
    public Map<String, Class<?>> getClasses() {
        return predefinedClasses;
    }

    @Override
    public IExpressionFactory clearModels() {
        predefinedModels.clear();
        return this;
    }

    @Override
    public IExpressionFactory removeModel(String name) {
        predefinedModels.remove(name);
        return this;
    }

    @Override
    public IExpressionFactory bindModel(String name, IModel<?> model) {
        predefinedModels.put(name, model);
        return this;
    }

    @Override
    public IExpressionFactory setModels(Map<String, IModel<?>> predefinedModels) {
        return this;
    }

    @Override
    public Map<String, IModel<?>> getModels() {
        return predefinedModels;
    }
}