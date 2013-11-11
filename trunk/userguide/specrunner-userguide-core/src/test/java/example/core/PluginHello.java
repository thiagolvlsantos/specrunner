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
package example.core;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;

/**
 * Example of plugin.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginHello extends AbstractPlugin {
    // CHECKSTYLE:OFF

    private int times;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        result.addResult(Success.INSTANCE, context.peek());
        return ENext.DEEP;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        super.doEnd(context, result);
    }
    // CHECKSTYLE:ON
}
