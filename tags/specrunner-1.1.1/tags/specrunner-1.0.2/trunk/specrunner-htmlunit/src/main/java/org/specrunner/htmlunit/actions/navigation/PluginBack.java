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
package org.specrunner.htmlunit.actions.navigation;

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.actions.AbstractPluginHistory;
import org.specrunner.result.IResultSet;

import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Call browser back.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginBack extends AbstractPluginHistory {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client, History history) throws IOException {
        history.back();
    }

}