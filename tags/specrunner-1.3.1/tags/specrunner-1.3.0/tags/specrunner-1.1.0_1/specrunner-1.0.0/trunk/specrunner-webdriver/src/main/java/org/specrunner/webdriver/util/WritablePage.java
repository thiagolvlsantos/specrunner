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
package org.specrunner.webdriver.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.specrunner.result.IWritable;
import org.specrunner.result.ResultException;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IHtmlUnitDriver;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Implements the page recording.
 * 
 * @author Thiago Santos
 * 
 */
public class WritablePage implements IWritable {

    private Map<String, Object> information;
    private File tmpDump;
    private File tmpSource;

    public WritablePage(WebDriver driver) {
        this(null, driver);
    }

    public WritablePage(Map<String, Object> information, WebDriver driver) {
        this.information = information;
        try {
            if (driver instanceof IHtmlUnitDriver) {
                dumpScreenShot((IHtmlUnitDriver) driver);
            } else if (driver instanceof TakesScreenshot) {
                dumpScreenshot(driver, (TakesScreenshot) driver);
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Driver type is " + (driver != null ? driver.getClass() : "null"));
                }
            }
        } catch (IOException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    protected void dumpScreenshot(WebDriver source, TakesScreenshot driver) throws IOException {
        Window window = source.manage().window();
        Dimension d = null;
        Point p = null;
        try {
            d = window.getSize();
            p = window.getPosition();
        } catch (Exception e) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Could not get size and position.");
            }
        }
        try {
            // write screen
            File scrFile = driver.getScreenshotAs(OutputType.FILE);
            tmpDump = File.createTempFile("crt", getExtension(scrFile));
            tmpDump.delete();
            FileUtils.copyFile(scrFile, tmpDump);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Saved page screen to temporary file " + tmpDump);
            }

            // write source
            tmpSource = File.createTempFile("crt", ".html");
            tmpSource.delete();
            FileUtils.writeStringToFile(tmpSource, source.getPageSource());
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Saved page source to temporary file " + tmpSource);
            }

        } catch (WebDriverException e) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Could not dump screenshot.");
            }
        } finally {
            if (d != null && p != null) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Restore size and location.");
                }
                window.setPosition(p);
                window.setSize(d);
            }
        }
    }

    protected String getExtension(File scrFile) {
        String name = scrFile.getName();
        name = name.substring(name.lastIndexOf("."), name.length());
        return name;
    }

    private void dumpScreenShot(IHtmlUnitDriver driver) throws IOException {
        WebClient wb = driver.getWebClient();
        if (wb != null) {
            WebWindow window = wb.getCurrentWindow();
            if (window != null) {
                Page page = window.getEnclosedPage();
                if (page instanceof HtmlPage) {
                    synchronized (page) {
                        tmpDump = File.createTempFile("crt", ".html");
                        tmpDump.delete();
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Saving page to temporary file " + tmpDump);
                        }
                        FileWriter fout = null;
                        try {
                            HtmlPage html = (HtmlPage) page;
                            // add imagens/css/etc.
                            html.save(tmpDump);
                            // remove XML readers.
                            fout = new FileWriter(tmpDump);
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
                    }
                } else {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Page type is " + (page != null ? page.getClass() : "null"));
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> getInformation() {
        return information;
    }

    @Override
    public Map<String, String> writeTo(String target) throws ResultException {
        Map<String, String> map = new HashMap<String, String>();
        if (tmpDump != null) {
            dump(tmpDump, target, map, "screen");
        }
        if (tmpSource != null) {
            dump(tmpSource, target, map, "source");
        }
        return map;
    }

    protected void dump(File from, String target, Map<String, String> map, String label) throws ResultException {
        File to = new File(target + getExtension(from));
        String name = from.getName();

        File fromFiles = new File(from.getParentFile(), name.substring(0, name.lastIndexOf('.')));
        File toFiles = new File(target);

        if (to.exists()) {
            if (!to.delete()) {
                throw new ResultException("Could not remove garbage '" + to + "'.");
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
        map.put(label, to.toURI().toString());
    }
}