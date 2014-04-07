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
package org.specrunner.plugins.core;

import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.type.Undefined;

/**
 * Default no-operation plugin.
 * 
 * @author Thiago Santos.
 * 
 */
public final class PluginNop extends AbstractPlugin {

    /**
     * A instance per thread.
     */
    private static ThreadLocal<IPlugin> instance = new ThreadLocal<IPlugin>() {
        @Override
        protected IPlugin initialValue() {
            return new PluginNop();
        };
    };

    /**
     * Hidden constructor.
     */
    private PluginNop() {
    }

    /**
     * Returns the singleton version of the plugin.
     * 
     * @return A plugin instance.
     */
    public static IPlugin emptyPlugin() {
        return instance.get();
    }

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public String toString() {
        return "nope";
    }
}