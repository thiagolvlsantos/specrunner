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
package org.specrunner.dumper.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nu.xom.Document;
import nu.xom.Serializer;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.dumper.ISourceDumper;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.result.IResultSet;
import org.specrunner.result.ResultException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.resource.IResource;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.util.UtilLog;

/**
 * A partial dumper which writes data to files.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractSourceDumperFile implements ISourceDumper {

    /**
     * Gap used to dump sources.
     */
    public static final int GAP = 4;

    /**
     * The output directory. The default value is 'src/test/resources/outcome'.
     */
    public static final String FEATURE_OUTPUT_DIRECTORY = AbstractSourceDumperFile.class.getName() + ".outputDirectory";
    /**
     * Default output directory.
     */
    protected File outputDirectory = new File("src/test/resources/outcome");

    /**
     * The output file name. The default name is the same of the input file.
     */
    public static final String FEATURE_OUTPUT_NAME = AbstractSourceDumperFile.class.getName() + ".outputName";
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
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_OUTPUT_DIRECTORY, "outputDirectory", File.class, this);
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
        try {
            IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
            fh.set(FEATURE_OUTPUT_NAME, "outputName", String.class, this);
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
     *             On result erorrs.
     */
    protected void clean(File file) throws ResultException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                clean(f);
            }
        }
        if (file.exists() && !file.delete()) {
            throw new ResultException("Could not delete " + (file.isDirectory() ? "directory" : "file") + " '" + file + "'.");
        }
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
    protected Serializer getSerializer(FileOutputStream fr) throws UnsupportedEncodingException {
        Serializer sr = new Serializer(fr, "ISO-8859-1");
        sr.setIndent(GAP);
        return sr;
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
        File parent = output.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new SourceDumperException("Could not create output directory '" + parent + "'.");
            }
        }
        FileOutputStream fr = null;
        try {
            fr = new FileOutputStream(output);
            Serializer sr = getSerializer(fr);
            sr.write(doc);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Get file prefix.
     * 
     * @return The file prefix.
     */
    protected String getFilePrefix() {
        return outputName + "_res/" + outputName.substring(0, outputName.lastIndexOf('.')) + "_frame/" + outputName.substring(0, outputName.lastIndexOf('.'));
    }

    /**
     * Append resources to a output file.
     * 
     * @param output
     *            The output file.
     * @throws SourceDumperException
     *             On dumper errors.
     */
    protected void appendResources(File output) throws SourceDumperException {
        File res = new File(output.getAbsoluteFile() + "_res");
        try {
            clean(res);
            ISource ref = SpecRunnerServices.get(ISourceFactory.class).newSource(output.getAbsolutePath());
            IResourceManager manager = ref.getManager();
            manager.addDefaultCss();
            manager.addDefaultJs();
            for (IResource r : manager) {
                r.writeTo(ref);
            }
            saveTo(ref.getDocument(), output);
        } catch (Exception e) {
            throw new SourceDumperException(e);
        }
    }
}