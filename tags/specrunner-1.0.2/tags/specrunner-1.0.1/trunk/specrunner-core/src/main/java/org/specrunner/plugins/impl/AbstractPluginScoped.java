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
package org.specrunner.plugins.impl;

import org.specrunner.context.IContext;

/**
 * A plugin with scope information. i.e. for declaring variables a scope can be
 * set.
 * 
 * @author Thiago Santos
 * 
 */
public class AbstractPluginScoped extends AbstractPluginNamed {

    private String scope;

    /**
     * The scope value.
     * 
     * @return The scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets scope. i.e. if scope is set to 'body' means that a local variable
     * must be associated to the body context.
     * 
     * @param scope
     *            A new scope.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Save the value to the context, using the given scope, if specified,
     * otherwise to the local scope.
     * 
     * @param context
     *            The context.
     * @param name
     *            The variable name.
     * @param value
     *            The variable value.
     */
    protected void saveLocal(IContext context, String name, Object value) {
        if (scope == null) {
            context.saveLocal(name, value);
        } else {
            context.saveScoped(scope, name, value);
        }
    }

    /**
     * Save the value to the context, using the given scope, if specified,
     * otherwise to the global scope.
     * 
     * @param context
     *            The context.
     * @param name
     *            The variable name.
     * @param value
     *            The variable value.
     */
    protected void saveGlobal(IContext context, String name, Object value) {
        if (scope == null) {
            context.saveGlobal(name, value);
        } else {
            context.saveScoped(scope, name, value);
        }
    }
}
