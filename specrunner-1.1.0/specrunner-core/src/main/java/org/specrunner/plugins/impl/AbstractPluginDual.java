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

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

/**
 * A generic plugin which performs a test, it the result is true, a success
 * result is added, otherwise adds an error to the result.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDual extends AbstractPluginValue {

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        Object obj = getValue(node.getValue(), isEval(), context);
        if (operation(obj, context)) {
            result.addResult(Success.INSTANCE, context.newBlock(node, this));
        } else {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), getError());
        }
    }

    /**
     * Stand for the testing operation.
     * 
     * @param obj
     *            The value to be used.
     * @param context
     *            The context.
     * @return true status must be OK, false otherwise, in which case error
     *         should be set.
     * @throws PluginException
     *             On operation errors.
     */
    protected abstract boolean operation(Object obj, IContext context) throws PluginException;

    /**
     * Gets the error, if the <code>operation</code> returns false.
     * 
     * @return The failure.
     */
    protected abstract Throwable getError();
}