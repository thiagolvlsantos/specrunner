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
package org.specrunner.htmlunit.result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.specrunner.result.IWritable;
import org.specrunner.result.ResultException;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Implements the page recording.
 * 
 * @author Thiago Santos
 * 
 */
public class WritablePage implements IWritable {

    /**
     * Map of information.
     */
    private final Map<String, Object> information;
    /**
     * Reference to temporary file dumped.
     */
    private File tmp;

    /**
     * Writable page by web driver.
     * 
     * @param page
     *            The client page.
     */
    public WritablePage(Page page) {
        this(null, page);
    }

    /**
     * The writable with extra information plus client page.
     * 
     * @param information
     *            The extra information.
     * @param page
     *            The client page.
     */
    public WritablePage(Map<String, Object> information, Page page) {
        this.information = information;
        try {
            if (page instanceof HtmlPage) {
                tmp = File.createTempFile("srunner", ".html");
                tmp.delete();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Saving page to temporary file " + tmp);
                }
                FileWriter fout = null;
                try {
                    HtmlPage html = (HtmlPage) page;
                    // add imagens/css/etc.
                    html.save(tmp);
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Page information could not be saved.");
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                } finally {
                    try {
                        if (fout != null) {
                            fout.close();
                        }
                    } catch (IOException e) {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace(e.getMessage(), e);
                        }
                    }
                }
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Page type is " + (page != null ? page.getClass() : "null"));
                }
            }
        } catch (IOException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean hasInformation() {
        return information != null && !information.isEmpty();
    }

    @Override
    public Map<String, Object> getInformation() {
        return information;
    }

    @Override
    public Map<String, String> writeTo(String target) throws ResultException {
        Map<String, String> map = new HashMap<String, String>();
        if (tmp != null) {
            dump(tmp, target, map, "source");
        }
        return map;
    }

    /**
     * Dump the file from temporary to target.
     * 
     * @param from
     *            The temporary file.
     * @param target
     *            The target.
     * @param map
     *            The map of informations.
     * @param label
     *            The label to be used.
     * @throws ResultException
     *             On dump errors.
     */
    protected void dump(File from, String target, Map<String, String> map, String label) throws ResultException {
        File to = new File(target + getExtension(from));
        try {
            move(from, to);
        } catch (IOException e) {
            throw new ResultException(e);
        }
        String name = from.getName();
        try {
            File dir = new File(from.getParentFile(), name.substring(0, name.lastIndexOf('.')));
            if (dir.exists()) {
                move(dir, new File(to.getParentFile(), dir.getName()));
            }
        } catch (IOException e) {
            throw new ResultException(e);
        }
        map.put(label, to.toURI().toString());
    }

    /**
     * Gets the file extension.
     * 
     * @param scrFile
     *            The source.
     * @return The corresponding file extension.
     */
    protected String getExtension(File scrFile) {
        String name = scrFile.getName();
        name = name.substring(name.lastIndexOf('.'), name.length());
        return name;
    }

    /**
     * Move files/directory.
     * 
     * @param from
     *            The original file/directory.
     * @param to
     *            The target file/directory.
     * @throws IOException
     *             On move errors.
     * @throws ResultException
     *             On action errors.
     */
    protected void move(File from, File to) throws IOException, ResultException {
        if (!from.exists()) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.debug("File/Dir " + from + " not exists.");
            }
            return;
        }
        if (to.exists()) {
            if (!to.delete()) {
                throw new ResultException("Could not remove resources '" + to + "'.");
            }
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Moving " + from + " to " + to + ".");
        }
        if (from.isDirectory()) {
            FileUtils.moveDirectory(from, to);
        } else {
            FileUtils.moveFile(from, to);
        }
    }
}
