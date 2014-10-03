/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.htmlunit.assertions;

import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * Check select options.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginOptions extends AbstractPluginSelection {

    @Override
    protected int checkSelection(IContext context, IResultSet result, WebClient client, Page page, HtmlElement element) throws PluginException {
        Node node = context.getNode();
        Nodes expectedOptions = node.query("descendant::li  | descendant::option");
        List<HtmlOption> currentOptions = ((HtmlSelect) element).getOptions();
        return testList(context, result, page, expectedOptions, currentOptions, true);
    }
}
