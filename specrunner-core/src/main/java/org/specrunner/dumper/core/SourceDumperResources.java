/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.dumper.core;

import java.io.File;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.resource.IResource;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.util.UtilLog;

/**
 * Dumps resources related to the source. To add resources use the
 * IResourceManager related to the source.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperResources extends AbstractSourceDumperFile {

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        File res = new File(outputFile.getAbsoluteFile() + "_res");
        try {
            clean(res);
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                throw new SourceDumperException("Output file could not be created.");
            }
            IResourceManager manager = source.getManager();
            ISource target = SRServices.get(ISourceFactoryManager.class).newSource(outputFile.getAbsolutePath());
            for (IResource r : manager) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("RESOURCE (" + r + "):" + res.getAbsolutePath());
                }
                r.writeTo(target);
            }
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("RESOURCES SAVED TO " + res.getAbsolutePath());
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        }
    }
}
