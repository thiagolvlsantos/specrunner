/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.impl.FinderXPath;

/**
 * A partial implementation of a plugin which finds elements in pages to perform
 * operations.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFind extends AbstractPluginBrowserAware {

    /**
     * Feature to set finder type.
     */
    public static final String FEATURE_FINDER_TYPE = AbstractPluginFind.class.getName() + ".finder";
    /**
     * The finder class name.
     */
    protected String finder;

    /**
     * Feature to set finderInstance.
     */
    public static final String FEATURE_FINDER_INSTANCE = AbstractPluginFind.class.getName() + ".finderInstance";
    /**
     * A finder instance.
     */
    protected IFinder finderInstance;

    /**
     * Default constructor.
     */
    public AbstractPluginFind() {
    }

    /**
     * The finder type.
     * 
     * @return The finder type.
     */
    public String getFinder() {
        return finder;
    }

    /**
     * Set the finder type.
     * 
     * @param finder
     *            The type.
     */
    public void setFinder(String finder) {
        this.finder = finder;
    }

    /**
     * Sets the plugin finderInstance.
     * 
     * @return The finderInstance.
     */
    public IFinder getFinderInstance() {
        return finderInstance;
    }

    /**
     * Set the finder.
     * 
     * @param finder
     *            A finder.
     */
    public void setFinderInstance(IFinder finder) {
        this.finderInstance = finder;
    }

    /**
     * Propagate parameters added to finderInstance.
     * 
     * @param context
     *            The context.
     * @return The finder configured.
     * @throws PluginException
     *             On processing errors.
     */
    public IFinder getFinderInstance(IContext context) throws PluginException {
        UtilParametrized.setProperties(context, finderInstance, getParameters().getAllParameters());
        return finderInstance;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (finder == null) {
            fm.set(FEATURE_FINDER_TYPE, this);
        }
        if (finderInstance == null) {
            fm.set(FEATURE_FINDER_INSTANCE, this);
        }
        if (finder != null && finderInstance == null) {
            try {
                finderInstance = (IFinder) Class.forName(finder).newInstance();
            } catch (Exception e) {
                throw new PluginException("IFinder implementation not found or invalid.", e);
            }
        }
        if (finderInstance == null) {
            finderInstance = FinderXPath.get();
        }
        finderInstance.reset();
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        List<WebElement> list = getFinderInstance(context).find(context, result, client);
        WebElement[] elements = list.toArray(new WebElement[list.size()]);
        if (UtilLog.LOG.isInfoEnabled()) {
            for (int i = 0; i < elements.length; i++) {
                UtilLog.LOG.info((getActionType() instanceof Command ? "Before" : "    On") + "[" + i + "]: " + getClass().getSimpleName() + "." + finderInstance.resume(context) + " on " + asString(elements[i]));
            }
        }
        process(context, result, client, elements);
        if (getActionType() instanceof Command) {
            if (UtilLog.LOG.isInfoEnabled()) {
                for (int i = 0; i < elements.length; i++) {
                    UtilLog.LOG.info(" After[" + i + "]: " + getClass().getSimpleName() + "." + finderInstance.resume(context) + " on " + asString(elements[i]));
                }
            }
        }
    }

    /**
     * Method delegation which receives the elements to be used by subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The browser.
     * @param elements
     *            The elements filtered by search strategy.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException;

    /**
     * Show the element as Strings.
     * 
     * @param element
     *            The element.
     * @return A string representation.
     */
    public String asString(WebElement element) {
        if (UtilLog.LOG.isDebugEnabled()) {
            try {
                return element.getTagName() + "." + getText(element);
            } catch (Exception e) {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
                try {
                    return element.getText();
                } catch (Exception e1) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e1.getMessage(), e1);
                    }
                }
            }
        }
        return String.valueOf(element);
    }

    /**
     * Get the value of element. Depends on element type.
     * 
     * @param element
     *            The element.
     * @return The value.
     */
    protected String getText(WebElement element) {
        String tagName = element.getTagName().toLowerCase();
        boolean isText = !("input".equals(tagName) || "textarea".equals(tagName));
        return isText ? element.getText() : element.getAttribute("value");
    }
}