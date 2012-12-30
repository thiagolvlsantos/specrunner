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
package org.specrunner.plugins.impl.elements;

import java.io.File;
import java.net.URI;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.element.impl.ImgResource;
import org.specrunner.util.UtilLog;

/**
 * Plugin to add image resources.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginImg extends AbstractPluginResources {

    /**
     * Static sequential number.
     */
    private static ThreadLocal<Integer> serialNumber = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * The image source.
     */
    private String src;

    /**
     * The image source attribute.
     * 
     * @return The source.
     */
    public String getSrc() {
        return src;
    }

    /**
     * Set source object.
     * 
     * @param src
     *            The image source.
     */
    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (src != null && isSave()) {
            try {
                Element img = (Element) context.getNode();

                ISource s = context.getSources().peek();
                ISourceFactory sf = s.getFactory();
                ISource o = sf.newSource(src);
                ISource r = s.resolve(o);
                URI path = r.getURI();
                String strPath = String.valueOf(path);

                ImgResource imgEle = new ImgResource(s, strPath, false, EType.BINARY, img);
                s.getManager().add(imgEle);

                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Img " + src + " from source " + s.getString() + " added.");
                }

                File file = null;
                File outDir = (File) SpecRunnerServices.get(IFeatureManager.class).get(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY);
                String outFile = (String) SpecRunnerServices.get(IFeatureManager.class).get(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME);
                if (outDir != null || outFile != null) {
                    file = new File(outDir, outFile);
                } else {
                    ISource first = context.getSources().getLast();
                    file = first.getFile();
                }
                String newName = file.getName() + "_res/" + serialNumber.get() + "_" + strPath.substring(strPath.lastIndexOf('/') + 1);
                serialNumber.set(serialNumber.get() + 1);

                img.addAttribute(new Attribute("src", newName));

                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Added to " + file + " as " + newName + ".");
                }
            } catch (SourceException e) {
                throw new PluginException(e);
            }
        }
        return ENext.DEEP;
    }
}
