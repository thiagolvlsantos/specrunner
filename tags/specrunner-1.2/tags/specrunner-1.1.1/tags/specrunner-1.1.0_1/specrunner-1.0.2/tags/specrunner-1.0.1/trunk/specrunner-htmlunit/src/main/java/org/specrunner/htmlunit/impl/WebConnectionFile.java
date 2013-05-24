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
package org.specrunner.htmlunit.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.ICacheable;
import org.specrunner.htmlunit.IWebConnection;
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
     * The cache directory. The default value is '
     * <code>src/test/resources/outcome/cache</code>'.
     */
    public static final String FEATURE_DIRECTORY = WebConnectionFile.class.getName() + ".cacheDirectory";
    protected File cacheDirectory = new File("src/test/resources/outcome/cache");

    /**
     * When true, clear file cache on first use of this WebConnection.
     */
    public static final String FEATURE_CLEAN = WebConnectionFile.class.getName() + ".cacheClean";
    protected Boolean cacheClean;

    /**
     * The cache strategy. The default value is ' <code>CacheableMime.</code>'.
     */
    public static final String FEATURE_STRATEGY = WebConnectionFile.class.getName() + ".strategy";
    protected String strategy;
    protected ICacheable strategyInstance = new CacheableMime();

    private MessageDigest digest;

    public WebConnectionFile(WebClient webClient) {
        super(webClient);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_DIRECTORY, "cacheDirectory", File.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (cacheClean == null) {
            try {
                fh.set(FEATURE_CLEAN, "cacheClean", Boolean.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (cacheClean != null && cacheClean) {
            reset();
        }
        if (strategy == null) {
            try {
                fh.set(FEATURE_STRATEGY, "strategy", ICacheable.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
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

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public Boolean getCacheClean() {
        return cacheClean;
    }

    public void setCacheClean(Boolean cacheClean) {
        this.cacheClean = cacheClean;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public void reset() {
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

    protected File asFile(WebRequest request) {
        digest.reset();
        digest.update(request.getUrl().toString().getBytes());
        BigInteger i = new BigInteger(1, digest.digest());
        String name = String.valueOf(i).replace(",", "");
        File result = new File(cacheDirectory, name);
        result.getParentFile().mkdirs();
        return result;
    }

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