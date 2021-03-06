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
package org.specrunner.webdriver;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.PrepareDefault;

/**
 * A partial implementation of a plugin which finds elements in pages to perform
 * operations.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFind extends AbstractPluginBrowserAware {

    /**
     * Feature to set plugins always wait for XPath corresponding component to
     * appear.
     */
    public static final String FEATURE_ALWAYS_WAIT_FOR = AbstractPluginFind.class.getName() + ".alwaysWaitFor";
    /**
     * The finder class name.
     */
    protected Boolean alwaysWaitFor;

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
     * Element prepare instance.
     */
    protected IPrepare prepare;

    /**
     * Get if automatic waitfor is enabled. Default is (client !=
     * HtmlUnitDriver), HtmlUnit wait is efficient.
     * 
     * @return true, if enabled, otherwise, false.
     */
    public Boolean getAlwaysWaitFor() {
        return alwaysWaitFor;
    }

    /**
     * Set always wait state.
     * 
     * @param alwaysWaitFor
     *            The wait flag.
     */
    public void setAlwaysWaitFor(Boolean alwaysWaitFor) {
        this.alwaysWaitFor = alwaysWaitFor;
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
     * Get element initializer.
     * 
     * @return A initializer.
     */
    public IPrepare getPrepare() {
        return prepare;
    }

    /**
     * Set element initializer.
     * 
     * @param prepare
     *            A initializer.
     */
    public void setPrepare(IPrepare prepare) {
        this.prepare = prepare;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_ALWAYS_WAIT_FOR, this);
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
        fm.set(IPrepare.FEATURE_PREPARE, this);
        if (prepare == null) {
            prepare = new PrepareDefault();
        }
    }

    @Override
    public IWait getWaitInstance(IContext context, IResultSet result, WebDriver client) throws PluginException {
        IWait instance = super.getWaitInstance(context, result, client);
        if (alwaysWaitFor == null) {
            alwaysWaitFor = !(client instanceof HtmlUnitDriver);
        }
        if (instance.getWaitfor() != null) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Specified wait for: " + instance.getWaitfor());
            }
        } else if (instance.isWaitForClient(context, result, client) && alwaysWaitFor) {
            instance.setWaitfor(getFinderInstance(context).getXPath(context));
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Automatic wait for: " + instance.getWaitfor());
            }
        }
        return instance;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        List<WebElement> list = getFinderInstance(context).find(context, result, client);
        if (list.isEmpty()) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("None element found for " + getFinderInstance().resume(context) + "."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
            return;
        }
        printBefore(context, list);
        WebElement[] elements = list.toArray(new WebElement[list.size()]);
        if (prepare != null) {
            prepare.prepare(this, client, elements);
        }
        process(context, result, client, elements);
        printAfter(context, list);
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

    /**
     * Print elements.
     * 
     * @param context
     *            A context.
     * @param list
     *            Elements list.
     * @throws PluginException
     *             On print errors.
     */
    protected void printBefore(IContext context, List<WebElement> list) throws PluginException {
        if (UtilLog.LOG.isInfoEnabled()) {
            for (int i = 0; i < list.size(); i++) {
                UtilLog.LOG.info((getActionType() instanceof Command ? "Before" : "    On") + "[" + i + "]: " + getClass().getSimpleName() + "." + finderInstance.resume(context) + " on " + asString(list.get(i)));
            }
        }
    }

    /**
     * Print elements.
     * 
     * @param context
     *            A context.
     * @param list
     *            Elements list.
     * @throws PluginException
     *             On print errors.
     */
    protected void printAfter(IContext context, List<WebElement> list) throws PluginException {
        if (getActionType() instanceof Command) {
            if (UtilLog.LOG.isInfoEnabled()) {
                for (int i = 0; i < list.size(); i++) {
                    UtilLog.LOG.info(" After[" + i + "]: " + getClass().getSimpleName() + "." + finderInstance.resume(context) + " on " + asString(list.get(i)));
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
