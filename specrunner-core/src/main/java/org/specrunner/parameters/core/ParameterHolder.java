/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.parameters.core;

import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.IParameterHolder;

/**
 * Implementation of parameter holder.
 * 
 * @author Thiago Santos
 * 
 */
public class ParameterHolder implements IParameterHolder {

    /**
     * The parameters decorator.
     */
    protected IParameterDecorator parameters;

    /**
     * Default constructor.
     */
    public ParameterHolder() {
        parameters = new ParameterDecoratorImpl();
        parameters.setDecorated(this);
    }

    @Override
    public IParameterDecorator getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(IParameterDecorator parameters) {
        this.parameters = parameters;
    }
}
