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
package org.specrunner.htmlunit.impl;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.IWait;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Wait delegator.
 * 
 * @author Thiago Santos
 * 
 */
public class WaitDelegator implements IWait {

    /**
     * Target.
     */
    private IWait delegate;

    /**
     * Default constructor.
     * 
     * @param delegate
     *            A wait algorithm.
     */
    public WaitDelegator(IWait delegate) {
        this.delegate = delegate;
    }

    @Override
    public IParameterDecorator getParameters() {
        return delegate.getParameters();
    }

    @Override
    public void setParameters(IParameterDecorator parameters) {
        delegate.setParameters(parameters);
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public Long getInterval() {
        return delegate.getInterval();
    }

    @Override
    public void setInterval(Long interval) {
        delegate.setInterval(interval);
    }

    @Override
    public Long getMaxwait() {
        return delegate.getMaxwait();
    }

    @Override
    public void setMaxwait(Long maxwait) {
        delegate.setMaxwait(maxwait);
    }

    @Override
    public boolean isWaitForClient(IContext context, IResultSet result, WebClient client) {
        return delegate.isWaitForClient(context, result, client);
    }

    @Override
    public void waitForClient(IContext context, IResultSet result, WebClient client) throws PluginException {
        delegate.waitForClient(context, result, client);
    }
}
