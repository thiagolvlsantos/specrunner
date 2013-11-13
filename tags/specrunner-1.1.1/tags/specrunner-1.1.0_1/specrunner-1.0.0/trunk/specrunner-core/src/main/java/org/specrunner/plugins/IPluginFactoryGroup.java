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

import org.specrunner.util.composite.IComposite;

/**
 * A plugin group factory. A node can be associated to more than on plugin. i.e.
 * for CSS classes based plugins, if more than one plugin is specified a plugin
 * group can be created, it depends on the plugin factory bound in
 * SpecRunnerServices.
 * 
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginFactoryGroup extends IPluginFactory, IComposite<IPluginFactoryGroup, IPluginFactory> {
}