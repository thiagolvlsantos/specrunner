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
package org.specrunner.htmlunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.impl.WaitDefault;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;

/**
 * A generic plugin that acts over a browser.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginBrowserAware extends AbstractPluginValue {

    /**
     * Feature to set wait element.
     */
    public static final String FEATURE_IWAIT = AbstractPluginBrowserAware.class.getName() + ".iwait";

    /**
     * IWait implementation.
     */
    protected IWait iwait;

    /**
     * Default directory to save downloaded files.
     */
    public static final String FEATURE_DIR = AbstractPluginBrowserAware.class.getName() + ".dir";

    /**
     * The output directory.
     */
    private String dir;

    /**
     * If and action result in downloading a file, this attribute specify where
     * download will take place on disk.
     */
    private String download;

    /**
     * Return current IWait implementer.
     * 
     * @return An IWait instance.
     */
    public IWait getIwait() {
        return iwait;
    }

    /**
     * Set wait algorithm.
     * 
     * @param iwait
     *            A wait implementer.
     */
    public void setIwait(IWait iwait) {
        this.iwait = iwait;
    }

    /**
     * Get current download directory, if any.
     * 
     * @return The directory.
     */
    public String getDir() {
        return dir;
    }

    /**
     * Set current download directory, if any.
     * 
     * @param dir
     *            The directory.
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * The download target.
     * 
     * @return The file target name.
     */
    public String getDownload() {
        return download;
    }

    /**
     * Set download name.
     * 
     * @param download
     *            The download file name.
     */
    public void setDownload(String download) {
        this.download = download;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_TIMEOUT, this);
        fm.set(FEATURE_IWAIT, this);
        if (iwait == null) {
            setIwait(new WaitDefault());
        }
        iwait.reset();
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        String tmp = getBrowserName();
        WebClient client = (WebClient) context.getByName(tmp);
        if (client == null) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Browser instance named '" + tmp + "' not created. See PluginBrowser."));
            return;
        }
        IWait instance = getWaitInstance(context, result, client);
        if (instance.isWaitForClient(context, result, client)) {
            instance.waitForClient(context, result, client);
        }
        doEnd(context, result, client);
        if (download != null) {
            saveDownload(context, result, client);
        }
    }

    /**
     * Propagate parameters added to iwait.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param client
     *            A client.
     * @return The iwait configured.
     * @throws PluginException
     *             On processing errors.
     */
    public IWait getWaitInstance(IContext context, IResultSet result, WebClient client) throws PluginException {
        UtilParametrized.setProperties(context, iwait, getParameters().getAllParameters());
        return iwait;
    }

    /**
     * Save the downloaded file, if it exists.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param client
     *            The client.
     * @throws PluginException
     *             On download errors.
     */
    private void saveDownload(IContext context, IResultSet result, WebClient client) throws PluginException {
        WebWindow window = client.getCurrentWindow();
        if (window != null) {
            Page tmp = window.getEnclosedPage();
            if (tmp instanceof UnexpectedPage) {
                WebResponse response = tmp.getWebResponse();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Binary file: " + response.getContentType());
                }
                UnexpectedPage up = (UnexpectedPage) tmp;
                IFeatureManager fm = SRServices.getFeatureManager();
                File outputDirectory = dir != null ? new File(dir) : (File) fm.get(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY);
                File outputFile = new File(outputDirectory, download);
                File outputParent = outputFile.getAbsoluteFile().getParentFile();
                if (!outputParent.exists()) {
                    if (!outputParent.mkdirs()) {
                        throw new PluginException("Could not create binary target directory:" + outputParent.getAbsolutePath());
                    }
                }
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Download file to '" + outputFile.getAbsolutePath() + "'.");
                }
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = up.getInputStream();
                    out = new FileOutputStream(outputFile);
                    UtilIO.writeTo(in, out);
                } catch (FileNotFoundException e) {
                    throw new PluginException(e);
                } catch (IOException e) {
                    throw new PluginException(e);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        throw new PluginException(e);
                    }
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        throw new PluginException(e);
                    }
                }
                Node node = context.getNode();
                if (node instanceof Element) {
                    Element span = new Element("span");
                    UtilNode.setIgnore(span);
                    span.addAttribute(new Attribute("class", "binary"));
                    span.appendChild(" [");

                    Element a = new Element("a");
                    a.addAttribute(new Attribute("href", download));
                    a.appendChild(outputFile.getName() + " (" + response.getContentType() + ")");
                    span.appendChild(a);

                    span.appendChild("] ");

                    Element e = (Element) node;
                    e.appendChild(span);
                }
            }
            try {
                window.getHistory().back();
            } catch (IOException e) {
                throw new PluginException(e);
            }
        }
    }

    /**
     * Gets the browser name.
     * 
     * @return The name.
     */
    public String getBrowserName() {
        return getName() != null ? getName() : PluginBrowser.BROWSER_NAME;
    }

    /**
     * Method delegation which receives the browser to be used by subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The browser.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException;
}
