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
package org.specrunner.readers.core;

import java.io.IOException;
import java.net.URI;

import org.specrunner.context.IContext;
import org.specrunner.readers.IReader;
import org.specrunner.readers.ReaderException;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilIO;
import org.specrunner.util.xom.node.INodeHolder;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Reader of content of a specified file.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReaderFile implements IReader {

    @Override
    public void initialize() {
    }

    @Override
    public String read(IContext context, Object obj, Object[] args) throws ReaderException {
        Node node = null;
        if (obj instanceof Node) {
            node = (Node) obj;
        }
        if (obj instanceof INodeHolder) {
            node = ((INodeHolder) obj).getNode();
        }
        StringBuilder sb = new StringBuilder();
        Nodes ns = node.query("descendant::a[@href]");
        for (int i = 0; i < ns.size(); i++) {
            String file = ((Element) ns.get(i)).getAttribute("href").getValue();
            ISource source = context.getCurrentSource();
            try {
                URI uri = source.getURI();
                file = uri.resolve(file).getPath();
                file = file.substring(1);
                sb.append(UtilIO.readFile(file));
            } catch (IOException e) {
                throw new ReaderException(e);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean replaceContent() {
        return false;
    }
}
