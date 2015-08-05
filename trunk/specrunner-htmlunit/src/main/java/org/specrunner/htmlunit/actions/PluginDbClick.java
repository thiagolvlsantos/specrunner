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
package org.specrunner.htmlunit.actions;

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Double click a given element.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginDbClick extends AbstractPluginKeys {

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        try {
            if (shiftkey != null || ctrlkey != null || altkey != null) {
                element.dblClick(shiftkey != null ? shiftkey : false, ctrlkey != null ? ctrlkey : false, altkey != null ? altkey : false);
            } else {
                element.dblClick();
            }
            result.addResult(Success.INSTANCE, context.peek());
        } catch (IOException e) {
            result.addResult(Failure.INSTANCE, context.peek(), e);
        }
    }
}
