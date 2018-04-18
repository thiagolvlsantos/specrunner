/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
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

    /**
     * Feature to set Chrome path.
     */
    public static final String FEATURE_CHROME = WebDriverFactoryChrome.class.getName() + ".chrome";
    /**
     * The path Chrome path.
     */
    protected String chrome;

    /**
     * Feature to set web driver path.
     */
    public static final String FEATURE_DRIVER = WebDriverFactoryChrome.class.getName() + ".driver";
    /**
     * The driver path.
     */
    protected String driver;

    /**
     * Feature to set Chrome switches options.
     */
    public static final String FEATURE_SWITCHES = WebDriverFactoryChrome.class.getName() + ".switches";
    /**
     * The switches options.
     */
    protected String switches;

    /**
     * Default constructor.
     */
    public WebDriverFactoryChrome() {
        String path = System.getProperty("user.home") + "/AppData/Local/Google/Chrome/Application/";
        chrome = path + "chrome.exe";
        driver = path + "chromedriver.exe";

        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_CHROME, this);
        fm.set(FEATURE_DRIVER, this);
        fm.set(FEATURE_SWITCHES, this);
    }

    /**
     * Gets the Chrome path.
     * 
     * @return The path.
     */
    public String getChrome() {
        return chrome;
    }

    /**
     * Sets the Chrome path.
     * 
     * @param chrome
     *            The path.
     */
    public void setChrome(String chrome) {
        this.chrome = chrome;
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

    /**
     * Gets the switches.
     * 
     * @return The switches.
     */
    public String getSwitches() {
        return switches;
    }

    /**
     * Sets the switches.
     * 
     * @param switches
     *            The switches.
     */
    public void setSwitches(String switches) {
        this.switches = switches;
    }

    @Override
    public WebDriver create(String name, IContext context) throws PluginException {
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
