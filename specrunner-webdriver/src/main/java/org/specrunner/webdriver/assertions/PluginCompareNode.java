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
package org.specrunner.webdriver.assertions;

import nu.xom.Element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.expressions.ExpressionException;
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Compare strings.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareNode extends AbstractPluginFindSingle {

    /**
     * The contains flag.
     */
    private Boolean contains = false;

    /**
     * Defines the attribute comparison strategy as contains. For example, if
     * the specification says:
     * 
     * <pre>
     *      &ltdiv class="compareNode" by="id:idNode&gt;My name is &lt;span style="background-color:red;"&gt;Thiago&lt;/span&gt;&lt;/div&gt;
     * </pre>
     * 
     * And the system under test return the following for 'idNode':
     * 
     * <pre>
     *      My name is &lt;span style="background-color:red; font-family:Times New Roman"&gt;Thiago&lt;/span&gt;
     * </pre>
     * 
     * The result is comparison success, when <code>contains</code> is set to
     * true, otherwise comparison failure.
     * 
     * Default is <code>false</code>.
     * 
     * @return true, to use substring on attribute comparisons, false, to use
     *         equals comparison.
     */
    public Boolean getContains() {
        return contains;
    }

    /**
     * Set attribute comparison flag.
     * 
     * @param contains
     *            The value.
     */
    public void setContains(Boolean contains) {
        this.contains = contains;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        IExpressionFactory ef = SpecRunnerServices.get(IExpressionFactory.class);
        try {
            IExpression e = ef.create("$NODE", context);
            if (!PluginCompareUtils.compareNode(this, (Element) e.evaluate(context), element, context.peek(), context, result, client)) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Nodes do not match."), new WritablePage(client));
            }
        } catch (ExpressionException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
    }

    /**
     * Check if the element stand for a node.
     * 
     * @param element
     *            The element.
     * @return true, if is node comparison, false, otherwise.
     */
    public static boolean isNode(Element element) {
        CellAdapter ca = new CellAdapter(element);
        return ca.hasAttribute("type") && ca.getAttribute("type").equalsIgnoreCase("node");
    }
}