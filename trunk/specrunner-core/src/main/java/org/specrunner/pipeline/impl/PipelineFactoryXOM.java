package org.specrunner.pipeline.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Nodes;

import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.util.UtilLog;

/**
 * Default implementation of a pipeline factory using XOM.
 * 
 * @author Thiago Santos
 */
public class PipelineFactoryXOM extends PipelineFactoryImpl {
    /**
     * The XML reader.
     */
    private final Builder builder = new Builder();

    @Override
    public IPipeline newPipeline(Object source) throws PipelineException {
        IPipeline pipeline = null;
        InputStream in = null;
        try {
            String str = String.valueOf(source);
            in = ClassLoader.getSystemResourceAsStream(str);
            if (in == null) {
                File file = new File(str);
                if (!file.exists()) {
                    throw new PipelineException("Unnable to create pipeline for file '" + file + "'.");
                }
                in = new FileInputStream(file);
            }
            Nodes nodes = builder.build(in).query("//pipe");
            for (int i = 0; i < nodes.size(); i++) {
                if (pipeline == null) {
                    pipeline = new PipelineImpl();
                }
                pipeline.add((IPipe) Class.forName(nodes.get(i).getValue().trim()).newInstance());
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PipelineException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
        }
        return pipeline;
    }
}
