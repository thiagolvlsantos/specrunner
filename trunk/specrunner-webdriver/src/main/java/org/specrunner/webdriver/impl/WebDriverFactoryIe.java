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
package org.specrunner.webdriver.impl;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IWebDriverFactory;

/**
 * Creates a InternetExplorer web driver instance with
 * <code>InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS</code>
 * set to true.
 * 
 * @author Thiago Santos
 * 
 */
public class WebDriverFactoryIe implements IWebDriverFactory {

    /**
     * Feature to set web driver path.
     */
    public static final String FEATURE_DRIVER = WebDriverFactoryIe.class.getName() + ".driver";
    /**
     * The driver path.
     */
    protected String driver;

    /**
     * Default constructor.
     */
    public WebDriverFactoryIe() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fm.set(FEATURE_DRIVER, "driver", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * Gets the driver path.
     * 
     * @return The path.
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets the driver path.
     * 
     * @param driver
     *            The path.
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public WebDriver create(IContext context) throws PluginException {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Factory:" + getClass());
        }
        if (driver == null) {
            throw new PluginException("Missing IE driver path. Download 'IEDriverServer.exe' at http://code.google.com/p/selenium/downloads/list and set 'FEATURE_DRIVER' to the executable.");
        }
        File fDriver = new File(driver);
        if (!fDriver.exists()) {
            throw new PluginException("Missing IE driver at path:" + fDriver + ". Download 'IEDriverServer.exe' at http://code.google.com/p/selenium/downloads/list and set 'FEATURE_DRIVER' to the executable.");
        }
        System.setProperty("webdriver.ie.driver", fDriver.getAbsolutePath());
        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        return new InternetExplorerDriver(ieCapabilities);
    }
}
