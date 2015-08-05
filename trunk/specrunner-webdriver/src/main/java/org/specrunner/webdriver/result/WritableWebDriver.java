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
package org.specrunner.webdriver.result;

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
public class WritableWebDriver implements IWritable {

    /**
     * Map of information.
     */
    private final Map<String, Object> information;
    /**
     * Reference to temporary file dumped.
     */
    private File tmpDump;
    /**
     * Reference to the source file or writable resources.
     */
    private File tmpSource;

    /**
     * Writable page by web driver.
     * 
     * @param driver
     *            The driver.
     */
    public WritableWebDriver(WebDriver driver) {
        this(null, driver);
    }

    /**
     * The writable with extra information plus web driver.
     * 
     * @param information
     *            The extra information.
     * @param driver
     *            The web driver.
     */
    public WritableWebDriver(Map<String, Object> information, WebDriver driver) {
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

    @Override
    public boolean hasInformation() {
        return information != null && !information.isEmpty();
    }

    /**
     * Dump a screen shot of web driver which implements
     * <code>TakesScreenshot</code>.
     * 
     * @param source
     *            The web driver source.
     * @param driver
     *            The web driver casted to screenshot object.
     * @throws IOException
     *             On writing errors.
     */
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
            tmpDump = File.createTempFile("srunnerw", getExtension(scrFile));
            tmpDump.delete();
            FileUtils.copyFile(scrFile, tmpDump);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Saved page screen to temporary file " + tmpDump);
            }

            // write source
            tmpSource = File.createTempFile("srunnerw", ".html");
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
     * Dump screenshots for <code>HtmlUnitLocal</code> drivers.
     * 
     * @param driver
     *            The <code>HtmlUnitLocal</code> subclass driver.
     * @throws IOException
     *             When screenshot presents some problems.
     */
    private void dumpScreenShot(IHtmlUnitDriver driver) throws IOException {
        WebClient wb = driver.getWebClient();
        if (wb != null) {
            WebWindow window = wb.getCurrentWindow();
            if (window != null) {
                Page page = window.getEnclosedPage();
                if (page instanceof HtmlPage) {
                    synchronized (page) {
                        tmpDump = File.createTempFile("srunnerw", ".html");
                        tmpDump.delete();
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Saving page to temporary file " + tmpDump);
                        }
                        FileWriter fout = null;
                        try {
                            HtmlPage html = (HtmlPage) page;
                            // add imagens/css/etc.
                            html.save(tmpDump);
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
