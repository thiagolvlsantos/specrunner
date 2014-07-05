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
package org.specrunner.htmlunit.actions;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFind;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.impl.SelectableTextInput;

/**
 * Clear elements.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginClear extends AbstractPluginFind {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                if (!(elements[i] instanceof SelectableTextInput)) {
                    throw new PluginException("Only SelectableTextInput instances can be cleaned.");
                }
                SelectableTextInput sel = (SelectableTextInput) elements[i];
                sel.setText("");
            }
        }
        result.addResult(Success.INSTANCE, context.peek());
    }
}