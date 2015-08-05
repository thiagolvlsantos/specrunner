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
package org.specrunner.source.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Serializer;

import org.specrunner.SRServices;
import org.specrunner.source.SourceException;
import org.specrunner.source.core.AbstractSourceFactory;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.IPresenter;
import org.specrunner.util.xom.IPresenterManager;

/**
 * Default implementation of text|feature reader.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class SourceFactoryText extends AbstractSourceFactory {
    /**
     * Log gap.
     */
    protected static final int GAP = 4;

    @Override
    protected Document fromTarget(URI uri, String target, String encoding) throws SourceException {
        Element root = new Element("html");
        Document doc = new Document(root);
        {
            Element head = new Element("head");
            root.appendChild(head);
        }
        {
            Element body = new Element("body");
            root.appendChild(body);

            InputStream in = null;
            try {
                if (isFile(uri, target)) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Source from file:" + target);
                    }
                    in = new FileInputStream(new File(target));
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Source from URI:" + uri);
                    }
                    in = uri.toURL().openStream();
                }
                IFeatureReader sr = new FeatureReaderImpl();
                Feature feature = sr.load(in, encoding);
                String error = feature.validate();
                if (!error.isEmpty()) {
                    throw new SourceException("Invalid feature file(" + target + "):" + error);
                }

                IPresenter presenter = SRServices.get(IPresenterManager.class).get(feature.getClass().getName());
                Node node = presenter.asNode(feature);
                body.appendChild(node);
                if (UtilLog.LOG.isTraceEnabled()) {
                    Serializer dumper = new Serializer(System.out);
                    dumper.setIndent(GAP);
                    dumper.write(doc);
                }
            } catch (Exception e) {
                throw new SourceException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new SourceException(e);
                    }
                }
            }
        }
        return doc;
    }
}
