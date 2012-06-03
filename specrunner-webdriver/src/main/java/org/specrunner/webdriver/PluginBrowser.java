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
package org.specrunner.webdriver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.listeners.PageListener;

/**
 * Create a web driver instance.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginBrowser extends AbstractPluginScoped {

    /**
     * Default browser name. To set a different name use the attribute
     * 'name=&lt;name&gt;'.
     */
    public static final String BROWSER_NAME = "browser";
    /**
     * Suffix of browser type. To see or use browser type in test use
     * '${browser_type}' or ${<name>_type} if a name is specified.
     */
    public static final String BROWSER_TYPE = "type";

    /**
     * Feature to enable browser recording.
     */
    public static final String FEATURE_RECORDING = PluginBrowser.class.getName() + ".recording";
    /**
     * Recording status. Default is "DEBUG log enabled".
     */
    private Boolean recording = UtilLog.LOG.isDebugEnabled();

    /**
     * Feature to set web driver class name.
     */
    public static final String FEATURE_WEBDRIVER_TYPE = PluginBrowser.class.getName() + ".webdriver";
    /**
     * The web driver class name.
     */
    protected String webdriver;

    /**
     * Feature to set webdriver factory class name.
     */
    public static final String FEATURE_WEBDRIVER_FACTORY = PluginBrowser.class.getName() + ".webdriverFactory";
    /**
     * The web driver factory.
     */
    protected String webdriverfactory;

    /**
     * Feature to set web driver instance.
     */
    public static final String FEATURE_WEBDRIVER_INSTANCE = PluginBrowser.class.getName() + ".webdriverInstance";
    /**
     * The web driver instance.
     */
    protected WebDriver webdriverInstance;

    /**
     * Feature to set web driver reuse.
     */
    public static final String FEATURE_REUSE = PluginBrowser.class.getName() + ".reuse";
    /**
     * The reuse status.
     */
    private Boolean reuse = false;

    /**
     * Get the recording status. true, for recording, false, otherwise.
     * 
     * @return The recording status.
     */
    public Boolean getRecording() {
        return recording;
    }

    /**
     * Sets the recording status.
     * 
     * @param recording
     *            The status.
     */
    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    /**
     * Gets web driver class name.
     * 
     * @return The web driver class.
     */
    public String getWebdriver() {
        return webdriver;
    }

    /**
     * Sets class name.
     * 
     * @param webdriver
     *            The class name.
     */
    public void setWebdriver(String webdriver) {
        this.webdriver = webdriver;
    }

    /**
     * Gets web driver factory class name. Might implements
     * <code>IWebDriverFactory</code>.
     * 
     * @return The factory class name.
     */
    public String getWebdriverfactory() {
        return webdriverfactory;
    }

    /**
     * The web driver factory class name.
     * 
     * @param webdriverfactory
     *            The driver factory.
     */
    public void setWebdriverfactory(String webdriverfactory) {
        this.webdriverfactory = webdriverfactory;
    }

    /**
     * Gets the web driver instance.
     * 
     * @return The instance.
     */
    public WebDriver getWebdriverInstance() {
        return webdriverInstance;
    }

    /**
     * Set the webdriver instance.
     * 
     * @param webdriverInstance
     *            The instance.
     */
    public void setWebdriverInstance(WebDriver webdriverInstance) {
        this.webdriverInstance = webdriverInstance;
    }

    /**
     * Gets the reuse status. If reuse is true, the browser can be reused by
     * multiple tests if the browser name is the same.
     * 
     * @return The reuse status.
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * Set reuse status.
     * 
     * @param reuse
     *            Reuse status.
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    /**
     * Creates a browser.
     */
    public PluginBrowser() {
        setName(BROWSER_NAME);
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_RECORDING, "recording", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (webdriver == null) {
            try {
                fh.set(FEATURE_WEBDRIVER_TYPE, "webdriver", String.class, this);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver type is " + webdriver);
                }
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (webdriverfactory == null) {
            try {
                fh.set(FEATURE_WEBDRIVER_FACTORY, "webdriverfactory", String.class, this);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver factory is " + webdriverfactory);
                }
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (webdriverInstance == null) {
            try {
                fh.set(FEATURE_WEBDRIVER_INSTANCE, "webdriverInstance", WebDriver.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        IListenerManager fac = SpecRunnerServices.get(IListenerManager.class);
        if (recording) {
            fac.add(new PageListener(getName()));
        } else {
            fac.remove(getName());
        }
        IReusableManager reusables = SpecRunnerServices.get(IReusableManager.class);
        if (reuse) {
            Map<String, Object> cfg = new HashMap<String, Object>();
            cfg.put("webdriver", webdriver);
            cfg.put("webdriverfactory", webdriverfactory);
            cfg.put("webdriverInstance", webdriverInstance);
            IReusable<?> reusable = reusables.get(getName());
            if (reusable != null && reusable.canReuse(cfg)) {
                reusable.reset();
                save(context, (WebDriver) reusable.getObject());
                result.addResult(Success.INSTANCE, context.peek());
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Browser (" + getName() + ") reused.");
                }
                return ENext.DEEP;
            }
        }
        if (webdriver != null) {
            try {
                webdriverInstance = (WebDriver) Class.forName(webdriver).newInstance();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver of type " + webdriverInstance.getClass() + " created.");
                }
            } catch (Exception e) {
                throw new PluginException("WebDriver implementation not found or invalid.", e);
            }
        } else if (webdriverfactory != null) {
            try {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Using  " + webdriverfactory + " to created WebDriver.");
                }
                webdriverInstance = ((IWebDriverFactory) Class.forName(webdriverfactory).newInstance()).create(context);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver of type " + webdriverInstance.getClass() + " created by factory.");
                }
            } catch (Exception e) {
                throw new PluginException("IWebDriverFactory implementation not found or invalid.", e);
            }
        }
        if (webdriverInstance == null) {
            webdriverInstance = new HtmlUnitDriverLocal(true);
        }
        save(context, webdriverInstance);
        if (reuse) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("WebDriver reuse enabled.");
            }
            reusables.put(getName(), new AbstractReusable<WebDriver>(getName(), webdriverInstance) {
                @Override
                public boolean canReuse(Map<String, Object> extra) {
                    String localWebdriver = (String) extra.get("webdriver");
                    String localWebDriverFactory = (String) extra.get("webdriverfactory");
                    WebDriver localWebDriverInstance = (WebDriver) extra.get("webdriverInstance");
                    if (webdriver != null) {
                        return webdriver.equalsIgnoreCase(localWebdriver);
                    } else if (webdriverfactory != null) {
                        return webdriverfactory.equalsIgnoreCase(localWebDriverFactory);
                    } else if (webdriverInstance != null) {
                        return webdriverInstance == localWebDriverInstance;
                    } else if (webdriver == null && webdriverfactory == null) {
                        return true;
                    }
                    return false;
                }

                @Override
                public void reset() {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("WebDriver recycling '" + getName() + "'.");
                    }
                    if (webdriverInstance instanceof HtmlUnitDriver) {
                        webdriverInstance.close();
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("WebDriver '" + getName() + "' windows closed.");
                        }
                    }
                }

                @Override
                public void release() {
                    webdriverInstance.quit();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("WebDriver '" + getName() + "' quit.");
                    }
                }
            });
        }
        return ENext.DEEP;
    }

    /**
     * Save browser to the context.
     * 
     * @param context
     *            The context.
     * @param driver
     *            The driver.
     */
    protected void save(IContext context, WebDriver driver) {
        String str = getName() != null ? getName() : BROWSER_NAME;
        String type = str + "_" + BROWSER_TYPE;
        saveGlobal(context, str, driver);
        saveGlobal(context, UtilEvaluator.asVariable(type), driver.getClass().getSimpleName());
    }
}