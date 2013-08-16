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
package org.specrunner.plugins.impl;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.xom.UtilNode;

/**
 * A generic plugin which performs a test, it the result is true, a success
 * result is added, otherwise adds an error to the result.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDual extends AbstractPluginValue {

    /**
     * Adjust execution moment, if on start node or at the end. Default is
     * 'false', to perform on end.
     */
    private Boolean onstart = Boolean.FALSE;

    /**
     * Get if execute on start.
     * 
     * @return true, to perform on start, false, otherwise.
     */
    public Boolean isOnstart() {
        return onstart;
    }

    /**
     * Set on start flag.
     * 
     * @param onstart
     *            On start execution.
     */
    public void setOnstart(Boolean onstart) {
        this.onstart = onstart;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (onstart) {
            return perform(context, result);
        }
        return ENext.DEEP;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (!onstart) {
            perform(context, result);
        }
    }

    /**
     * Perform plugin action/assertion.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @return The next move.
     * @throws PluginException
     *             On plugin execution errors.
     */
    protected ENext perform(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        Object obj = isEval() ? UtilNode.newElementAdapter(node).getObject(context, true) : node.getValue();
        if (operation(obj, context)) {
            result.addResult(Success.INSTANCE, context.newBlock(node, this));
        } else {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), getError());
        }
        return ENext.DEEP;
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