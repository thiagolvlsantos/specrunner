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
package org.specrunner.source.resource.positional.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.EPlace;
import org.specrunner.source.resource.positional.Position;
import org.specrunner.util.UtilIO;

/**
 * Default resource to be written in header part.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResourceHeader extends AbstractResourcePositional {

    private static int serialNumber = 0;

    protected AbstractResourceHeader(ISource parent, String path, boolean classpath, EType type, Position position) {
        super(parent, path, classpath, type, position);
    }

    @Override
    public ISource writeTo(ISource output) throws ResourceException {
        try {
            Document doc = getParent().getDocument();
            Nodes places = doc.query(getPosition().getXpath());
            if (places.size() == 0) {
                places = doc.query("//html");
            }
            // if (places.size() == 0) {
            // places = doc.query("//html", new XPathContext("xmlns",
            // "http://www.w3.org/1999/xhtml"));
            // }
            if (places.size() > 0 && places.get(0) instanceof Element) {
                Element target = (Element) places.get(0);

                List<URL> urls = getResourceURLs();

                if (getType() == EType.TEXT) {
                    Element tag = getHeaderTag();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    UtilIO.writeAllTo(urls, out);
                    String content = out.toString();
                    out.close();
                    tag.appendChild(content);
                    if (getPosition().getPlace() == EPlace.START) {
                        target.insertChild(tag, 0);
                    } else {
                        target.appendChild(tag);
                    }
                } else {
                    for (URL url : urls) {
                        String file = url.toString();
                        String name = file.substring(file.lastIndexOf("/") + 1) + "_res_" + (serialNumber++);
                        Element tag = getHeaderTag(output, name);
                        File resFile = getFile(output, name);
                        if (!resFile.getParentFile().exists()) {
                            if (!resFile.getParentFile().mkdirs()) {
                                throw new ResourceException("Could not create resource directory '" + resFile.getParent() + "'.");
                            }
                        }
                        UtilIO.writeToClose(url.openStream(), new FileOutputStream(resFile));
                        if (getPosition().getPlace() == EPlace.START) {
                            target.insertChild(tag, 0);
                        } else {
                            target.appendChild(tag);
                        }
                    }
                }
            } else {
                throw new ResourceException("Head element not found.");
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        } catch (SourceException e) {
            throw new ResourceException(e);
        }
        return output;
    }

    protected abstract Element getHeaderTag();

    protected abstract Element getHeaderTag(ISource output, String name);

    protected abstract File getFile(ISource output, String name);

    @Override
    public String asString() {
        return null;
    }

    @Override
    public Node asNode() {
        return null;
    }

    @Override
    public String toString() {
        return "AbstractResourceHeader [path=" + getResourcePath() + ",type=" + getType() + ", position=" + getPosition() + "]";
    }
}