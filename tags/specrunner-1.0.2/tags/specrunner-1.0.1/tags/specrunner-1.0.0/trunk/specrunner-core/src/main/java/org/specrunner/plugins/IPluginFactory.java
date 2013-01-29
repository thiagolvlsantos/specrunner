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
package org.specrunner.plugins;

import nu.xom.Node;

import org.specrunner.context.IContext;

/**
 * Stands for a plugin factory.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginFactory {

    /**
     * Creates a new plugin.
     * 
     * @param source
     *            The source to create factory.
     * @param context
     *            The context.
     * @return A plugin based on source information.
     * @throws PluginException
     *             On creation error.
     */
    IPlugin newPlugin(Node source, IContext context) throws PluginException;
}
