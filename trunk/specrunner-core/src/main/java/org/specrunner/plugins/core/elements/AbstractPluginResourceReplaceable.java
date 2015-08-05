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
package org.specrunner.plugins.core.elements;

import java.io.File;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Plugin to replace resources.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginResourceReplaceable extends AbstractPluginResource {

    /**
     * Cache of resources per output file.
     */
    protected static ICache<String, String> pathToFile = SRServices.get(ICacheFactory.class).newCache(AbstractPluginResourceReplaceable.class.getName());

    /**
     * Static sequential number.
     */
    protected static ThreadLocal<Integer> serialNumber = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        String reference = getReferenceValue();
        if (reference != null && isSave()) {
            try {
                Element element = (Element) context.getNode();

                ISource source = context.getCurrentSource();
                ISourceFactory sourceFactory = source.getFactory();
                ISource referencedSource = sourceFactory.newSource(reference);
                ISource relative = source.resolve(referencedSource);
                String path = String.valueOf(relative.getURI());

                File file = null;
                File outDir = (File) SRServices.getFeatureManager().get(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY);
                String outFile = (String) SRServices.getFeatureManager().get(ConstantsDumperFile.FEATURE_OUTPUT_NAME);
                if (outDir != null || outFile != null) {
                    file = new File(outDir, outFile);
                } else {
                    ISource first = context.getSources().getLast();
                    file = first.getFile();
                }

                String key = file + path;
                String newName = pathToFile.get(key);
                if (newName == null) {
                    addResource(source, path, element);
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Resource " + reference + " from source " + source.getString() + " added.");
                    }
                    newName = file.getName() + "_res/" + serialNumber.get() + "_" + path.substring(path.lastIndexOf('/') + 1);
                    serialNumber.set(serialNumber.get() + 1);
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Adding to " + file + " as " + newName + ".");
                    }
                    pathToFile.put(key, newName);
                } else {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Reuse of " + newName + ".");
                    }
                }
                replaceName(element, newName);
            } catch (SourceException e) {
                throw new PluginException(e);
            }
        }
        return ENext.DEEP;
    }

    /**
     * Get attribute reference name.
     * 
     * @return The attribute name.
     */
    protected abstract String getReferenceName();

    /**
     * Get the previous attribute value.
     * 
     * @return The attribute (reference) value.
     */
    protected abstract String getReferenceValue();

    /**
     * Add the resource as a reference.
     * 
     * @param source
     *            The source.
     * @param path
     *            The target path.
     * @param element
     *            The resource element.
     */
    protected abstract void addResource(ISource source, String path, Element element);

    /**
     * Replace old reference by the new created.
     * 
     * @param element
     *            The element.
     * @param newName
     *            The attribute new value.
     */
    protected void replaceName(Element element, String newName) {
        element.addAttribute(new Attribute(getReferenceName(), newName));
    }
}
