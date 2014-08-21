/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.plugins.core;

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.core.ParameterDecoratorImpl;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.composite.core.CompositeImpl;

/**
 * Default plugin group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginGroupImpl extends CompositeImpl<IPluginGroup, IPlugin> implements IPluginGroup {

    /**
     * The plugin parent factory.
     */
    private IPluginFactory parent;

    /**
     * Parameter holder.
     */
    private IParameterDecorator parameters;

    /**
     * Index of last children not skipped.
     */
    private int index = 0;

    /**
     * Default constructor.
     */
    public PluginGroupImpl() {
        parameters = new ParameterDecoratorImpl() {
            @Override
            public Object setParameter(String name, Object value, IContext context) throws Exception {
                for (IPlugin p : getChildren()) {
                    p.getParameters().setParameter(name, value, context);
                }
                return super.setParameter(name, value, context);
            }
        };
        parameters.setDecorated(this);
    }

    @Override
    public IPluginFactory getParent() {
        return parent;
    }

    @Override
    public void setParent(IPluginFactory parent) {
        this.parent = parent;
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
            index++;
            try {
                ENext n = p.doStart(context, result);
                if (n == ENext.SKIP) {
                    exit = ENext.SKIP;
                    break;
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
        int i = 0;
        for (IPlugin p : getChildren()) {
            i++;
            if (i > index) {
                break;
            }
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
