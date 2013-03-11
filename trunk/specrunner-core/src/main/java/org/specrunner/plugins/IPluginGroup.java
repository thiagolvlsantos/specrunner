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
package org.specrunner.plugins;

import org.specrunner.util.composite.IComposite;

/**
 * A group plugin.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginGroup extends IPlugin, IComposite<IPluginGroup, IPlugin> {

    /**
     * Normalizes the plugin group. For example, if the group has only
     * <code>PluginNop</code> instances, only one instance is enough to reflect
     * the functionality, other example is a group with only one element, the
     * normalized version is the contained plugin itself.
     * 
     * @return The normalized group.
     */
    IPlugin getNormalized();
}