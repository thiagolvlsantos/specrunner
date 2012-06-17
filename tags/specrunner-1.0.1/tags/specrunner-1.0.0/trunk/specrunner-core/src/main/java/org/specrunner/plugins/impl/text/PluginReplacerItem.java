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
package org.specrunner.plugins.impl.text;

import java.io.StringReader;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;

/**
 * Replace items.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginReplacerItem extends AbstractPlugin {

    protected void replaceText(Node node, IContext context, String text, String result) throws PluginException {
        if (!text.equals(result)) {
            try {
                StringReader sr = new StringReader(result);
                ISource s = SpecRunnerServices.get(ISourceFactory.class).newSource(sr);
                Node newNode = s.getDocument().getRootElement().getChild(1).copy();
                node.getParent().replaceChild(node, newNode);
                context.setNode(newNode);
                sr.close();
            } catch (SpecRunnerException e) {
                throw new PluginException(e);
            }
        } else {
            ((Text) node).setValue(result);
        }
    }
}