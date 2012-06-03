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
package org.specrunner.context.impl;

import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextFactory;
import org.specrunner.pipeline.IChannel;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;

/**
 * Default context factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ContextFactoryImpl implements IContextFactory {

    @Override
    public IContext newContext(IChannel channel, ISource source, IRunner runner) throws ContextException {
        return new ContextImpl(channel, source, runner);
    }
}
