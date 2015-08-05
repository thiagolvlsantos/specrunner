/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.plugins.core.text;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Replace text elements by their evaluated expressions.
 * <p>
 * Example: <br>
 * <blockquote> Current time in miliseconds is ${System.currentTimeMilis()}.
 * </blockquote> Will be evaluated as: <blockquote> Current time in miliseconds
 * is 90999384384747. </blockquote>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginReplacer extends AbstractPlugin {
    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node e = context.getNode();
        if (e instanceof Text) {
            String text = e.getValue();
            String replaced = UtilEvaluator.replace(text, context, false);
            if (UtilLog.LOG.isDebugEnabled() && !text.equals(replaced)) {
                UtilLog.LOG.debug("replacer_before>" + text);
                UtilLog.LOG.debug("replacer_after>" + replaced + "." + replaced.getClass());
            }
            ((Text) e).setValue(replaced);
        }
        return ENext.DEEP;
    }
}
