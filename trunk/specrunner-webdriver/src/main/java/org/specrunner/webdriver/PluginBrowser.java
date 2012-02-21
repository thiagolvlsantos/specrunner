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
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.result.IResultSet;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.actions.IAction;
import org.specrunner.webdriver.listeners.PageListener;

public class PluginBrowser extends AbstractPluginScoped implements IAction {

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
     * Default browser recording feature.
     */
    public static final String FEATURE_RECORDING = PluginBrowser.class.getName() + ".recording";
    private Boolean recording = true;

    /**
     * Default WebDriver type.
     */
    public static final String FEATURE_WEBDRIVER_TYPE = PluginBrowser.class.getName() + ".webdriver";
    protected String webdriver;

    /**
     * Default WebDriver factory.
     */
    public static final String FEATURE_WEBDRIVER_FACTORY = PluginBrowser.class.getName() + ".webdriverFactory";
    protected String webdriverfactory;

    /**
     * Default finderInstance feature.
     */
    public static final String FEATURE_WEBDRIVER_INSTANCE = PluginBrowser.class.getName() + ".webdriverInstance";
    protected WebDriver webdriverInstance;

    /**
     * Default browser setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginBrowser.class.getName() + ".reuse";
    private Boolean reuse = false;

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    public String getWebdriver() {
        return webdriver;
    }

    public void setWebdriver(String webdriver) {
        this.webdriver = webdriver;
    }

    public String getWebdriverfactory() {
        return webdriverfactory;
    }

    public void setWebdriverfactory(String webdriverfactory) {
        this.webdriverfactory = webdriverfactory;
    }

    public WebDriver getWebdriverInstance() {
        return webdriverInstance;
    }

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

    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    public PluginBrowser() {
        setName(BROWSER_NAME);
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_RECORDING, "recording", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            if (webdriver == null) {
                fh.set(FEATURE_WEBDRIVER_TYPE, "webdriver", String.class, this);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver type is " + webdriver);
                }
            }
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            if (webdriverfactory == null) {
                fh.set(FEATURE_WEBDRIVER_FACTORY, "webdriverfactory", String.class, this);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebDriver factory is " + webdriverfactory);
                }
            }
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_WEBDRIVER_INSTANCE, "webdriverInstance", WebDriver.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
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
            cfg.put("webdriverFactory", webdriverfactory);
            IReusable reusable = reusables.get(getName());
            if (reusable != null && reusable.canReuse(cfg)) {
                reusable.reset();
                save(context, (WebDriver) reusable.getObject());
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
            reusables.put(getName(), new AbstractReusable(getName(), webdriverInstance) {
                @Override
                public boolean canReuse(Map<String, Object> extra) {
                    String localWebdriver = (String) extra.get("webdriver");
                    String localWebDriverFactory = (String) extra.get("webdriverFactory");
                    if (webdriver != null) {
                        return webdriver.equalsIgnoreCase(localWebdriver);
                    } else if (webdriverfactory != null) {
                        return webdriverfactory.equalsIgnoreCase(localWebDriverFactory);
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

    protected void save(IContext context, WebDriver driver) {
        String str = getName() != null ? getName() : BROWSER_NAME;
        String type = str + "_" + BROWSER_TYPE;
        saveGlobal(context, str, driver);
        saveGlobal(context, UtilEvaluator.asVariable(type), driver.getClass().getSimpleName());
    }
}