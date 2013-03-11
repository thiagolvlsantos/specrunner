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
package org.specrunner.hibernate;

import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.type.Command;

/**
 * Common part of plugin with Spring-linke factory methods.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFactory extends AbstractPluginValue {

    /**
     * The provider class name.
     */
    protected String type;
    /**
     * The factory class.
     */
    protected String factory;
    /**
     * The method name in object factory.
     */
    protected String method;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    /**
     * Get the session factory provider class name.
     * 
     * @return The session factory provider class name.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the session factory provider.
     * 
     * @param type
     *            The factory type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the session factory factory class.
     * 
     * @return The object factory.
     */
    public String getFactory() {
        return factory;
    }

    /**
     * Set session factory object factory.
     * 
     * @param factory
     *            The object factory name.
     */
    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * Get the static method of object factory.
     * 
     * @return The creator method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the method of a factory.
     * 
     * @param method
     *            The factory method.
     */
    public void setMethod(String method) {
        this.method = method;
    }

}