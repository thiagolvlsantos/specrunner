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

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.impl.ParameterDecoratorImpl;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default plugin group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginGroupImpl extends CompositeImpl<IPluginGroup, IPlugin> implements IPluginGroup {

    /**
     * Parameter holder.
     */
    private IParameterDecorator parameters;

    /**
     * Default constructor.
     */
    public PluginGroupImpl() {
        parameters = new ParameterDecoratorImpl();
        parameters.setDecorated(this);
    }

    /**
     * Adds a child plugin. No repetition is allowed.
     * 
     * @param child
     *            The child plugin.
     * @return The group itself.
     */
    @Override
    public IPluginGroup add(IPlugin child) {
        if (child != PluginNop.emptyPlugin() && !getChildren().contains(child)) {
            return super.add(child);
        }
        return this;
    }

    /**
     * Returns the normalized version of the group. In case of empty group a
     * singleton instance of PluginNop is returned, and in case of single member
     * group the member is returned.
     * 
     * @return The normalized version of the plugin.
     */
    @Override
    public IPlugin getNormalized() {
        return !isEmpty() ? (getChildren().size() == 1 ? getChildren().get(0) : this) : PluginNop.emptyPlugin();
    }

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        for (IPlugin p : getChildren()) {
            p.initialize(context);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        ENext exit = ENext.DEEP;
        for (IPlugin p : getChildren()) {
            try {
                ENext n = p.doStart(context, result);
                if (n == ENext.SKIP) {
                    exit = ENext.SKIP;
                }
                if (context.isChanged()) {
                    break;
                }
            } catch (Exception e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        }
        return exit;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        for (IPlugin p : getChildren()) {
            try {
                p.doEnd(context, result);
            } catch (Exception e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        }
    }

    @Override
    public IPlugin copy(IContext context) throws PluginException {
        IPluginGroup group = new PluginGroupImpl();
        for (IPlugin p : getChildren()) {
            group.add(p.copy(context));
        }
        return group;
    }

    @Override
    public IParameterDecorator getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(IParameterDecorator parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "GROUP" + getChildren();
    }
}
