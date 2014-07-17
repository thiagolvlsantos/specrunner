/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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

import nu.xom.Element;
import nu.xom.Node;

import org.openqa.selenium.WebDriver;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IWebDriverFactory;

/**
 * Creates a <code>HtmlUnitDriverLocal(true)</code> web driver instance.
 * 
 * @author Thiago Santos
 * 
 */
public class WebDriverFactoryHtmlUnit implements IWebDriverFactory {

    /**
     * Enable webdriver instance reuse.
     */
    private Boolean reuse = Boolean.FALSE;

    /**
     * Current instance.
     */
    private HtmlUnitDriverLocal driver;

    /**
     * Get reuse flag.
     * 
     * @return true, if reuse enabled, false, otherwise.
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * Set reuse flag.
     * 
     * @param reuse
     *            The flag state.
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public WebDriver create(String name, IContext context) {
        SRServices.getFeatureManager().set(FEATURE_REUSE, this);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Default factory( reuse = " + reuse + "," + this + "):" + getClass());
        }
        if (reuse) {
            if (driver == null) {
                driver = newInstance(name, context);
            }
        } else {
            driver = newInstance(name, context);
        }
        return driver;
    }

    /**
     * Creates a new instance.
     * 
     * @param name
     *            A instance name.
     * @param context
     *            A context.
     * @return A webdriver instance.
     */
    protected HtmlUnitDriverLocal newInstance(String name, IContext context) {
        HtmlUnitDriverLocal driver = new HtmlUnitDriverLocal(true);
        driver.setName(name);
        Node node = context.getNode();
        if (node instanceof Element) {
            try {
                UtilParametrized.setProperties(context, driver, (Element) node);
            } catch (PluginException e) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(e.getMessage(), e);
                }
            }
        }
        driver.initialize();
        return driver;
    }
}