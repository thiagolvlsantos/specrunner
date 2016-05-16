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

import static org.specrunner.dumper.core.ConstantsDumperFile.DEFAULT_OUTPUT_DIRECTORY;
import static org.specrunner.dumper.core.ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY;
import static org.specrunner.dumper.core.ConstantsDumperFile.FEATURE_OUTPUT_NAME;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Document;
import nu.xom.Serializer;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.dumper.ISourceDumper;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.result.IResultSet;
import org.specrunner.result.ResultException;
import org.specrunner.source.ISource;
import org.specrunner.source.core.EncodedImpl;
import org.specrunner.util.UtilLog;

/**
 * A partial dumper which writes data to files.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractSourceDumperFile extends EncodedImpl implements ISourceDumper {

    /**
     * Gap used to dump sources.
     */
    public static final int GAP = 4;

    /**
     * Clean attribute.
     */
    protected Boolean clean = DEFAULT_CLEAN;
    /**
     * Default output directory.
     */
    protected File outputDirectory = DEFAULT_OUTPUT_DIRECTORY;
    /**
     * Output file name.
     */
    protected String outputName = null;
    /**
     * Output file (absolute).
     */
    protected File outputFile;

    /**
     * Set source and result objects.
     * 
     * @param source
     *            The source.
     * @param result
     *            The result.
     * @throws SourceDumperException
     *             On setting errors.
     */
    protected void set(ISource source, IResultSet result) throws SourceDumperException {
        setFeatures(source);
        outputFile = new File(outputDirectory + File.separator + outputName);
    }

    @Override
    public Boolean getClean() {
        return clean;
    }

    @Override
    public void setClean(Boolean clean) {
        this.clean = clean;
    }

    /**
     * The output directory.
     * 
     * @return The output.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Set the output directory.
     * 
     * @param outputDirectory
     *            The new output.
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output file name.
     * 
     * @return The output name.
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     * Set the output file name.
     * 
     * @param outputName
     *            The output name.
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    /**
     * Get output file.
     * 
     * @return The output file.
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Set the output file.
     * 
     * @param outputFile
     *            The file.
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Set features.
     * 
     * @param source
     *            The specification source.
     * @throws SourceDumperException
     *             On dumper error.
     */
    protected void setFeatures(ISource source) throws SourceDumperException {
        outputDirectory();
        outputName(source);
    }

    /**
     * Gets output directory based on configuration or features.
     * 
     * @throws SourceDumperException
     *             On dumper error.
     */
    protected void outputDirectory() throws SourceDumperException {
        IFeatureManager fm = SRServices.getFeatureManager();
        try {
            fm.setStrict(FEATURE_OUTPUT_DIRECTORY, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        }
        if (outputDirectory == null) {
            throw new SourceDumperException("Output directory should be set.");
        }
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new SourceDumperException("Could not create outputFile directory '" + outputDirectory + "'.");
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Directory outputFile '" + outputDirectory + "' created.");
                }
            }
        }
    }

    /**
     * Set the output file for the source.
     * 
     * @param source
     *            The reference source.
     * @throws SourceDumperException
     *             On dumper errors.
     */
    protected void outputName(ISource source) throws SourceDumperException {
        File asFile = source.getFile();
        if (asFile != null) {
            outputName = asFile.getName();
        }
        IFeatureManager fm = SRServices.getFeatureManager();
        try {
            fm.setStrict(FEATURE_OUTPUT_NAME, this);
        } catch (SpecRunnerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        }
        if (outputName == null) {
            throw new SourceDumperException("Output file not set.");
        }
    }

    /**
     * Clean up directory files before dumping resources.
     * 
     * @param file
     *            The reference file.
     * @throws ResultException
     *             On cleanup errors.
     */
    protected void clean(File file) throws ResultException {
        if (clean) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    clean(f);
                }
            }
            if (file.exists() && !file.delete()) {
                throw new ResultException("Could not delete " + (file.isDirectory() ? "directory" : "file") + " '" + file + "'.");
            }
        }
    }

    /**
     * Get detail report name.
     * 
     * @return The detail report name.
     */
    protected String detailReport() {
        return outputName + "_res" + File.separator + outputName.substring(0, outputName.lastIndexOf('.')) + "_details.html";
    }

    /**
     * Gets document serializer.
     * 
     * @param fr
     *            The output stream.
     * @return The serializer.
     * @throws UnsupportedEncodingException
     *             On enconding errors.
     */
    protected Serializer getSerializer(OutputStream fr) throws UnsupportedEncodingException {
        return new Serializer(fr, getEncoding());
    }

    /**
     * Save a document to a output file.
     * 
     * @param doc
     *            The document.
     * @param output
     *            The target file.
     * @throws SourceDumperException
     *             On dumper errors.
     */
    protected void saveTo(Document doc, File output) throws SourceDumperException {
        checkParent(output);
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            fout = new FileOutputStream(output);
            bout = new BufferedOutputStream(fout);
            getSerializer(bout).write(doc);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Check output directory.
     * 
     * @param output
     *            The output.
     * @throws SourceDumperException
     *             On directory errors.
     */
    protected void checkParent(File output) throws SourceDumperException {
        File parent = output.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new SourceDumperException("Could not create output directory '" + parent + "'.");
        }
    }
}
