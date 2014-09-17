package org.specrunner.pipeline.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.IPipeListener;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.source.core.UtilEncoding;
import org.specrunner.util.UtilLog;

/**
 * Default implementation of a pipeline factory reading from file.
 * 
 * @author Thiago Santos
 */
public class PipelineFactoryCustom extends PipelineFactoryImpl {

    @Override
    public IPipeline newPipeline(SRServices services, Object source) throws PipelineException {
        IPipeline pipeline = new PipelineImpl();
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
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
            inr = new InputStreamReader(in, UtilEncoding.getEncoding(services.lookup(IFeatureManager.class)));
            br = new BufferedReader(inr);

            String line = br.readLine();
            String lis = "<listener>";
            String pip = "<pipe>";
            while (line != null) {
                line = line.trim();
                if (line.startsWith(pip)) {
                    pipeline.add((IPipe) Class.forName(line.substring(pip.length(), line.length() - pip.length() - 1)).newInstance());
                } else if (line.startsWith(lis)) {
                    pipeline.addPipelineListener((IPipeListener) Class.forName(line.substring(lis.length(), line.length() - lis.length() - 1)).newInstance());
                }
                line = br.readLine();
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
            if (inr != null) {
                try {
                    inr.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
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
