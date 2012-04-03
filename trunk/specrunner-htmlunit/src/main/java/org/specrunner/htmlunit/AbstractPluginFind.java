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
package org.specrunner.htmlunit;

import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.actions.IAction;
import org.specrunner.htmlunit.impl.FinderXPath;
import org.specrunner.parameters.impl.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * A partial implementation of a plugin which finds elements in SgmlPage to
 * perform operations.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFind extends AbstractPluginSgml {

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
    private IFinder finderInstance;

    /**
     * Sets the plugin finderInstance.
     * 
     * @return The finderInstance.
     */
    public IFinder getFinder() {
        return finderInstance;
    }

    /**
     * Set the finder.
     * 
     * @param finder
     *            A finder.
     */
    public void setFinder(IFinder finder) {
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
        UtilParametrized.setProperties(context, finderInstance, getAllParameters());
        return finderInstance;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        if (finder == null) {
            try {
                fh.set(FEATURE_FINDER_TYPE, "finder", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (finderInstance == null) {
            try {
                fh.set(FEATURE_FINDER_INSTANCE, "finderInstance", IFinder.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (finder != null) {
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
    protected void doEnd(IContext context, IResultSet result, WebClient client, SgmlPage page) throws PluginException {
        List<?> list = getFinderInstance(context).find(context, result, client, page);
        HtmlElement[] elements = list.toArray(new HtmlElement[list.size()]);
        if (UtilLog.LOG.isInfoEnabled()) {
            for (int i = 0; i < elements.length; i++) {
                UtilLog.LOG.info((this instanceof IAction ? "Before" : "    On") + "[" + i + "]: " + getClass().getSimpleName() + "." + finderInstance.resume(context) + " on " + asString(elements[i]));
            }
        }
        process(context, result, client, page, elements);
        if (this instanceof IAction) {
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
     * @param page
     *            The page.
     * @param elements
     *            The elements filtered by search strategy.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException;

    /**
     * Show the element as Strings.
     * 
     * @param element
     *            The element.
     * @return A string representation.
     */
    public String asString(HtmlElement element) {
        if (UtilLog.LOG.isDebugEnabled()) {
            return element.asXml().trim();
        }
        return String.valueOf(element);
    }
}