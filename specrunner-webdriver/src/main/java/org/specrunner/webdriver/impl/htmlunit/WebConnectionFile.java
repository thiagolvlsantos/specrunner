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
package org.specrunner.webdriver.impl.htmlunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * A web connection which cache resources like CSS and JS to file system. The
 * implementation of ICacheable is responsible for filtering those elements
 * which are supposed to be cached.
 * 
 * @author Thiago Santos.
 * 
 */
public class WebConnectionFile extends HttpWebConnection implements IWebConnection {

    /**
     * The cache directory feature. The default value is '
     * <code>src/test/resources/outcome/cache</code>'.
     */
    public static final String FEATURE_DIRECTORY = WebConnectionFile.class.getName() + ".cacheDirectory";
    /**
     * The cache directory.
     */
    protected File cacheDirectory = new File("src/test/resources/outcome/cache");

    /**
     * When true, clear file cache on first use of this WebConnection.
     */
    public static final String FEATURE_CLEAN = WebConnectionFile.class.getName() + ".cacheClean";
    /**
     * Clean cache status.
     */
    protected Boolean cacheClean;

    /**
     * The cache strategy. The default value is ' <code>CacheableMime.</code>'.
     */
    public static final String FEATURE_STRATEGY = WebConnectionFile.class.getName() + ".strategy";
    /**
     * The cache strategy class.
     */
    protected String strategy;
    /**
     * The cache strategy instance. Default is <code>CachableMime</code>.
     */
    protected ICacheable strategyInstance = new CacheableMime();

    /**
     * A message digester utility for name mapping (url to MD5).
     */
    private MessageDigest digest;

    /**
     * Web connection from a client. The client.
     * 
     * @param webClient
     *            The client.
     */
    public WebConnectionFile(WebClient webClient) {
        super(webClient);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_DIRECTORY, this);
        if (cacheClean == null) {
            fm.set(FEATURE_CLEAN, this);
        }
        if (cacheClean != null && cacheClean) {
            reset();
        }
        if (strategy == null) {
            fm.set(FEATURE_STRATEGY, this);
        }
        if (strategy != null) {
            try {
                strategyInstance = (ICacheable) Class.forName(strategy).newInstance();
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new RuntimeException(e);
            }
        }

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the cache directory.
     * 
     * @return The cache directory.
     */
    public File getCacheDirectory() {
        return cacheDirectory;
    }

    /**
     * Sets the cache directory.
     * 
     * @param cacheDirectory
     *            The directory.
     */
    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * Gets the clean cache status.
     * 
     * @return The cache status.
     */
    public Boolean getCacheClean() {
        return cacheClean;
    }

    /**
     * Sets cache status.
     * 
     * @param cacheClean
     *            The cache status.
     */
    public void setCacheClean(Boolean cacheClean) {
        this.cacheClean = cacheClean;
    }

    /**
     * Sets the cache strategy class name.
     * 
     * @return The strategy.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the cache strategy class name.
     * 
     * @param strategy
     *            The strategy.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public void reset() {
        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdirs()) {
                throw new RuntimeException("Could not create cache directory '" + cacheDirectory + "'.");
            }
        }
        for (File f : cacheDirectory.listFiles()) {
            if (f.delete()) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Cache file '" + f + "' deleted.");
                }
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Cache file '" + f + "' could not be delete.");
                }
            }
        }
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        File file = asFile(request);
        WebResponse response = get(file);
        if (response == null) {
            response = super.getResponse(request);
            if (strategyInstance.isCacheable(request, response)) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Save to cache (loaded in " + response.getLoadTime() + " mls): " + request.getUrl());
                }
                save(request, response, file);
            }
        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loaded from cache: " + request.getUrl());
            }
        }
        return response;
    }

    /**
     * Gets the web response as a file.
     * 
     * @param request
     *            The source request.
     * @return The request as a file.
     */
    protected File asFile(WebRequest request) {
        digest.reset();
        digest.update(request.getUrl().toString().getBytes());
        BigInteger i = new BigInteger(1, digest.digest());
        String name = String.valueOf(i).replace(",", "");
        File result = new File(cacheDirectory, name);
        result.getParentFile().mkdirs();
        return result;
    }

    /**
     * Gets a web response from a file.
     * 
     * @param file
     *            The file.
     * @return The <code>WebResponse</code> corresponding to the file.
     * @throws IOException
     *             On response load errors.
     */
    protected WebResponse get(File file) throws IOException {
        WebResponse result = null;
        ObjectInputStream oin = null;
        try {
            if (file.exists()) {
                long time = System.currentTimeMillis();
                oin = new ObjectInputStream(new FileInputStream(file));
                result = (WebResponse) oin.readObject();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("File loaded in " + (System.currentTimeMillis() - time) + " mls.");
                }
            }
        } catch (ClassNotFoundException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        } finally {
            if (oin != null) {
                try {
                    oin.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Perform web response saving.
     * 
     * @param request
     *            The request.
     * @param response
     *            The response.
     * @param file
     *            The corresponding file.
     */
    protected void save(WebRequest request, WebResponse response, File file) {
        ObjectOutputStream out = null;
        try {
            long time = System.currentTimeMillis();
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(response);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Response saved in " + (System.currentTimeMillis() - time) + " mls.");
            }
        } catch (IOException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
