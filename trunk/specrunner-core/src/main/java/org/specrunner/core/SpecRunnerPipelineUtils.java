/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.IPipelineFactory;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.util.UtilLog;

/**
 * Abstract default implementation. All methods can be overridden!!!!
 * 
 * @author Thiago Santos
 * 
 */
public final class SpecRunnerPipelineUtils {

    /**
     * Pipeline file name in configurations.
     */
    public static final String PIPELINE_FILENAME = "sr_pipeline";

    /**
     * Map of available pipelines by file name.
     */
    private static Map<String, IPipeline> pipelines = new HashMap<String, IPipeline>();

    /**
     * Default constructor.
     */
    private SpecRunnerPipelineUtils() {
    }

    /**
     * Get pipeline by fixed file name.
     * 
     * @param service
     *            Service mapping instance.
     * @param file
     *            The file.
     * @return A pipeline.
     * @throws PipelineException
     *             On get errors.
     */
    public static IPipeline getPipeline(SRServices service, String file) throws PipelineException {
        return getPipeline(service, null, file);
    }

    /**
     * Get a pipeline from a configuration file.
     * 
     * @param services
     *            Current services instance.
     * @param configuration
     *            A configuration.
     * @param defaultFile
     *            The default file.
     * @return A pipeline instance.
     * @throws PipelineException
     *             On creation error.
     */
    public static IPipeline getPipeline(SRServices services, IConfiguration configuration, String defaultFile) throws PipelineException {
        IPipeline pipe = null;
        String name = String.valueOf(configuration == null ? defaultFile : configuration.get(PIPELINE_FILENAME, defaultFile));
        synchronized (pipelines) {
            long time = System.currentTimeMillis();
            pipe = pipelines.get(name);
            if (pipe == null) {
                pipe = services.lookup(IPipelineFactory.class).newPipeline(name);
                pipelines.put(name, pipe);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Pipe of '" + name + "' loaded in " + (System.currentTimeMillis() - time) + ".");
                }
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Pipe of '" + name + "' reused in " + (System.currentTimeMillis() - time) + ".");
                }
            }
        }
        return pipe;
    }
}