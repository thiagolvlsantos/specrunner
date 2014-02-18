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
package org.specrunner.webdriver.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IFinder;

/**
 * Utility class to manage search strategies to be used by browser aware
 * plugins. You can register XPath search strategies, and reuse then in your
 * tests. Patterns like search by id, name, value, a tag which contains
 * something, tags or attributes that start with something, or pure XPath are
 * already registered.
 * 
 * @author Thiago Santos
 * 
 */
public class FinderXPath extends ParameterHolder implements IFinder {

    /**
     * Thread safe instance of <code>IFinder</code>.
     */
    private static ThreadLocal<FinderXPath> instance = new ThreadLocal<FinderXPath>() {
        @Override
        protected FinderXPath initialValue() {
            return new FinderXPath();
        };
    };

    /**
     * Mapping of XPath strategies.
     */
    private final Map<String, String> strategies = new HashMap<String, String>();
    /**
     * The reference to document objects.
     */
    protected String by;
    /**
     * The separator of arguments in 'by' attribute.
     */
    protected String separator = ";";

    /**
     * Minimum constructor.
     */
    protected FinderXPath() {
        addStrategy("id", "//*[@id='{0}']");
        addStrategy("name", "//*[@name='{0}']");
        addStrategy("value", "//*[@value='{0}']");
        addStrategy("class", "//{0}[contains(@class,'{1}')]");
        addStrategy("contains", "//{0}[contains(.,'{1}')]");
        addStrategy("starts", "//{0}[starts-with({1},'{2}')]");
        addStrategy("ends", "//{0}[ends-with({1},'{2}')]");
        addStrategy("link", "//a[text()='{0}']");
        addStrategy("linkText", "//a[text()='{0}']");
        addStrategy("partialLinkText", "//a[contains(text(),'{0}')]");
        addStrategy("xpath", "{0}");
    }

    /**
     * Gets the thread safe instance of finder.
     * 
     * @return The finder instance.
     */
    public static FinderXPath get() {
        return instance.get();
    }

    /**
     * List of available strategies.
     * 
     * @return The list of strategies.
     */
    public Iterator<String> getStrategies() {
        return strategies.keySet().iterator();
    }

    /**
     * Map an new search strategy.
     * 
     * @param name
     *            The strategy name.
     * @param xpath
     *            The corresponding XPath with replace masks as ${0}, ${1}, and
     *            so on.
     */
    public void addStrategy(String name, String xpath) {
        strategies.put(name, xpath);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Search strategy (" + name + "," + xpath + ") added.");
        }
    }

    /**
     * Recover a given strategy.
     * 
     * @param name
     *            The strategy name.
     * @return The corresponding XPath.
     */
    public String findStrategy(String name) {
        return strategies.get(name);
    }

    /**
     * The search strategy to be used. i.e. <code>by="id:txtName"</code> means
     * find the element whose 'id' is 'txtName'. The 'by' attribute is closely
     * related to <code>FindeXPath</code> strategy.
     * <p>
     * The 'id' type is associated in FindeXPath to the XPath "//*[@id='{0}']",
     * is means that if you use 'by="id:txtName"', this plugin will lookup the
     * 'id' corresponding XPath, tokenize the content after ':' using ';' as
     * separator and replace '{...}' elements in order. To use another separator
     * set 'separator' attribute.
     * <p>
     * In the previous example, 'by=id:txtName' becomes a XPath expression
     * "//*[@id='txtName']".
     * 
     * @return The search strategy.
     */
    public String getBy() {
        return by;
    }

    /**
     * Sets the target reference.
     * 
     * @param by
     *            The reference.
     */
    public void setBy(String by) {
        this.by = by;
    }

    /**
     * Gets the parameter separator. Default is ";".
     * 
     * @return The separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Get the type of search strategy. i.e. 'id', or 'name'.
     * 
     * @return The search type, null, not specified.
     */
    public String getType() {
        if (by == null) {
            return null;
        }
        int pos = by.indexOf(':');
        if (pos > 0) {
            return by.substring(0, pos);
        }
        return by;
    }

    /**
     * Get the arguments for search strategy. Examples:
     * <ul>
     * <li>in '<code>by=contains:a;site</code>', the arguments will be
     * <code>{0}=a</code> and <code>{1}=site</code>, the corresponding XPath
     * will be <code>"//a[contains(.,'site')]"</code>;</li>
     * <li>in a plugin tag with
     * <code>&lt;span by="contains:a;"&gt;data&lt;span&gt;</code>, the arguments
     * will be <code>{0}=a</code> and <code>{1}=data</code>, the corresponding
     * XPath will be <code>"//a[contains(.,'data')]"</code>;</li>
     * <li>in a plugin tag with
     * <code>&lt;span by="contains:"&gt;a;data&lt;span&gt;</code>, the arguments
     * will be <code>{0}=a</code> and <code>{1}=data</code>, the corresponding
     * XPath will be <code>"//a[contains(.,'data')]"</code>;</li>
     * <li>in a plugin tag with
     * <code>&lt;span by="contains:" separator="|"&gt;a|data&lt;span&gt;</code>,
     * the arguments will be <code>{0}=a</code> and <code>{1}=data</code>, the
     * corresponding XPath will be <code>"//a[contains(.,'data')]"</code>;</li>
     * </ul>
     * 
     * @param context
     *            The test context.
     * @return The search strategy arguments.
     */
    public String[] getArguments(IContext context) {
        if (by == null) {
            return null;
        }
        int pos = by.indexOf(':');
        String value = by;
        if (pos > 0) {
            value = by.substring(pos + 1);
            if (value.isEmpty() || value.endsWith(getSeparator())) {
                value = value + context.getNode().getValue();
            }
            return value.split(getSeparator());
        } else {
            value = context.getNode().getValue();
            return value.split(getSeparator());
        }
    }

    /**
     * Gets the XPath related to the plugin.
     * 
     * @param context
     *            The test context.
     * @return The XPath expression to find elements.
     * @throws PluginException
     *             On XPath generation errors.
     */
    public String getXPath(IContext context) throws PluginException {
        String format = findStrategy(getType());
        if (format == null) {
            throw new PluginException("Invalid search strategy '" + getType() + "', see FindeXPath to see options, or registry your pattern using FindeXPath.get().addStrategy(<type>,<pattern>). i.e. if you add FindeXPath.addStrategy(\"class\",\"//*[contains(@class,'${0}')]\") means you can now use 'by=class:any_css'.");
        }
        String[] args = getArguments(context);
        for (int i = 0; i < args.length; i++) {
            format = format.replace("{" + i + "}", args[i]);
        }
        return format;
    }

    @Override
    public void reset() {
        by = null;
    }

    @Override
    public List<WebElement> find(IContext context, IResultSet result, WebDriver client) throws PluginException {
        if (by == null) {
            throw new PluginException("Element missing search strategy, use, for instance, the attribute by='id:txtName'.");
        }
        String type = getType();
        String xpath = getXPath(context);
        List<WebElement> list = null;
        try {
            list = client.findElements(By.xpath(xpath));
        } catch (Exception e) {
            throw new PluginException("Element " + type + " '" + xpath + "' not found.", e);
        }
        if (list == null) {
            throw new PluginException("Use \"by='<type>:<argument>'\" to select the element.");
        }
        return list;
    }

    @Override
    public String resume(IContext context) throws PluginException {
        return "strategy: by " + getBy() + " with XPath " + getXPath(context);
    }
}