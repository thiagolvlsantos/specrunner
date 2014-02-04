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
package org.specrunner.htmlunit.actions;

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Clicks a given element.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginClick extends AbstractPluginKeys {

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        try {
            if (shiftkey != null || ctrlkey != null || altkey != null) {
                element.click(shiftkey != null ? shiftkey : false, ctrlkey != null ? ctrlkey : false, altkey != null ? altkey : false);
            } else {
                element.click();
            }
            result.addResult(Status.SUCCESS, context.peek());
        } catch (IOException e) {
            result.addResult(Status.FAILURE, context.peek(), e);
        }
    }
}