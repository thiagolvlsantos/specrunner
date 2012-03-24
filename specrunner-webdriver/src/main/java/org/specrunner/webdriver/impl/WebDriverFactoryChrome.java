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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IWebDriverFactory;

/**
 * A helper class for Chrome WebDriver. Set the paths required.
 * 
 * @author Thiago Santos
 * 
 */
public class WebDriverFactoryChrome implements IWebDriverFactory {

    public static final String FEATURE_CHROME = WebDriverFactoryChrome.class.getName() + ".chrome";
    protected String chrome;

    public static final String FEATURE_DRIVER = WebDriverFactoryChrome.class.getName() + ".driver";
    protected String driver;

    public static final String FEATURE_SWITCHES = WebDriverFactoryChrome.class.getName() + ".switches";
    protected String switches;

    public WebDriverFactoryChrome() {
        String path = System.getProperty("user.home") + "/AppData/Local/Google/Chrome/Application/";
        chrome = path + "chrome.exe";
        driver = path + "chromedriver.exe";

        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fm.set(FEATURE_CHROME, "chrome", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fm.set(FEATURE_DRIVER, "driver", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fm.set(FEATURE_SWITCHES, "switches", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    public String getChrome() {
        return chrome;
    }

    public void setChrome(String chrome) {
        this.chrome = chrome;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getSwitches() {
        return switches;
    }

    public void setSwitches(String switches) {
        this.switches = switches;
    }

    @Override
    public WebDriver create(IContext context) throws PluginException {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Factory:" + getClass());
        }
        File fChrome = new File(chrome);
        if (!fChrome.exists()) {
            throw new PluginException("Missing Chrome application at:" + fChrome + ". Download Chrome and/or set 'FEATURE_CHROME' feature to the application executable.");
        }
        File fDriver = new File(driver);
        if (!fDriver.exists()) {
            throw new PluginException("Missing Chrome driver at Chrome path:" + fDriver + ". Download 'chromedriver.exe' at http://chromium.googlecode.com/ and set 'FEATURE_DRIVER' to the executable.");
        }
        System.setProperty("webdriver.chrome.driver", fDriver.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.setBinary(fChrome);
        if (switches != null) {
            options.addArguments(switches);
        }
        return new ChromeDriver(options);
    }
}
