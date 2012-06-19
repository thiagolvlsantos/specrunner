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
package org.specrunner.htmlunit.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                tmp = File.createTempFile("sr", ".html");
                tmp.delete();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Saving page to temporary file " + tmp);
                }
                FileWriter fout = null;
                try {
                    HtmlPage html = (HtmlPage) page;
                    // add imagens/css/etc.
                    html.save(tmp);
                    // remove XML readers.
                    fout = new FileWriter(tmp);
                    String xml = html.asXml();
                    fout.write(xml.substring(xml.indexOf('\n'), xml.lastIndexOf('\n')));
                    fout.close();
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
    public Map<String, Object> getInformation() {
        return information;
    }

    @Override
    public Map<String, String> writeTo(String target) throws ResultException {
        Map<String, String> result = new HashMap<String, String>();
        if (tmp != null) {
            File from = tmp;
            File to = new File(target + ".html");
            String name = from.getName();

            File fromFiles = new File(from.getParentFile(), name.substring(0, name.lastIndexOf('.')));
            File toFiles = new File(target);

            if (to.exists()) {
                if (!to.delete()) {
                    throw new ResultException("Could not remove screen scrap '" + to + "'.");
                }
            }
            from.renameTo(to);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Moving " + from + " to " + to + ".");
            }

            if (toFiles.exists()) {
                if (!toFiles.delete()) {
                    throw new ResultException("Could not remove screen scrap resources '" + toFiles + "'.");
                }
            }
            fromFiles.renameTo(toFiles);

            result.put("details", to.toURI().toString());
        }
        return result;
    }
}
