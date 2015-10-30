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
package org.specrunner.source.resource.positional.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.core.UtilEncoding;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.EPlace;
import org.specrunner.source.resource.positional.Position;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Default resource to be written in header part.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResourceHeader extends AbstractResourcePositional {

    /**
     * A sequential number.
     */
    private static ThreadLocal<Integer> serialNumber = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * Creates a header resource.
     * 
     * @param parent
     *            The source.
     * @param path
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     * @param position
     *            The resource position.
     */
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
            if (places.size() > 0 && places.get(0) instanceof Element) {
                Element target = (Element) places.get(0);
                List<URL> urls = getResourceURLs();
                if (getType() == EType.TEXT) {
                    Element tag = getHeaderTag();
                    ByteArrayOutputStream out = null;
                    try {
                        out = new ByteArrayOutputStream();
                        UtilIO.writeAllTo(urls, out);
                        String content = out.toString(UtilEncoding.getEncoding());
                        tag.appendChild(content.replace("\r\n", "\n"));
                    } finally {
                        out.close();
                    }
                    if (getPosition().getPlace() == EPlace.START) {
                        target.insertChild(tag, 0);
                    } else {
                        target.appendChild(tag);
                    }
                } else {
                    int i = 1;
                    for (URL url : urls) {
                        // if its is an absolute URL copy is not required
                        String protocol = url.getProtocol();
                        if (protocol != null && !protocol.toLowerCase().startsWith("http")) {
                            String file = url.toString();
                            String strName = file.substring(file.lastIndexOf('/') + 1);
                            int pos = strName.lastIndexOf('.');
                            String name = (pos > 0 ? strName.substring(0, pos) : strName) + "_r_" + serialNumber.get();
                            serialNumber.set(serialNumber.get() + 1);
                            Element tag = getHeaderTag(output, name);
                            File resFile = getFile(output, name);
                            if (!resFile.getParentFile().exists() && !resFile.getParentFile().mkdirs()) {
                                throw new ResourceException("Could not create resource directory '" + resFile.getParent() + "'.");
                            }
                            if (UtilLog.LOG.isTraceEnabled()) {
                                UtilLog.LOG.trace("Writting[" + (i++) + "/" + urls.size() + "] '" + url + "' to " + resFile);
                            }
                            UtilIO.writeToClose(url, resFile);
                            if (getPosition().getPlace() == EPlace.START) {
                                target.insertChild(tag, 0);
                            } else {
                                target.appendChild(tag);
                            }
                            addNode(tag);
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

    /**
     * Get the element which represents the resource.
     * 
     * @return The corresponding element.
     */
    protected abstract Element getHeaderTag();

    /**
     * Ges the element which represents the source provided extra information.
     * 
     * @param output
     *            The output source.
     * @param name
     *            The output name.
     * @return The corresponding element.
     */
    protected abstract Element getHeaderTag(ISource output, String name);

    /**
     * Get the file which represents the output resource.
     * 
     * @param output
     *            The output source.
     * @param name
     *            The name.
     * @return The result file.
     */
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractResourceHeader)) {
            return false;
        }
        AbstractResourceHeader arh = (AbstractResourceHeader) obj;
        return getResourcePath().equals(arh.getResourcePath()) && isClasspath() == arh.isClasspath() && getType() == arh.getType() && getPosition() == arh.getPosition();
    }
}
