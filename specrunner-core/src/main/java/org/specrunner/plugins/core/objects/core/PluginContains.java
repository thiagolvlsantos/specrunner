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
package org.specrunner.plugins.core.objects.core;

import org.specrunner.plugins.core.objects.AbstractPluginObjectCompare;
import org.specrunner.plugins.core.objects.IObjectManager;

/**
 * Check if a given list of objects is present in memory. The object output list
 * has the same format of input.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginContains extends AbstractPluginObjectCompare<IObjectManager> {

    /**
     * Create a output comparison.
     */
    public PluginContains() {
        super(ObjectSelector.get());
    }
}
