/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.HashMap;

import nu.xom.Builder;
import nu.xom.Element;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.core.ComparatorNode;
import org.specrunner.context.IContext;
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.source.IBuilderFactory;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.string.IStringNormalizer;
import org.specrunner.util.string.UtilString;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Compare nodes.
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
     * Tell the plugin to use comparator.
     */
    private Boolean strict = false;

    /**
     * Plugin default comparator.
     */
    private ThreadLocal<IComparator> comparator = new ThreadLocal<IComparator>() {
        @Override
        protected IComparator initialValue() {
            return new ComparatorNode();
        };
    };

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

    /**
     * Gets the strict flag.
     * 
     * @return true, to strict comparison, false, otherwise.
     */
    public Boolean getStrict() {
        return strict;
    }

    /**
     * Set strict comparator.
     * 
     * @param strict
     *            The comparator flag.
     */
    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        try {
            IExpressionFactory ef = SRServices.getExpressionFactory();
            IExpression e = ef.create("$NODE", context);
            Element expected = (Element) e.evaluate(context);
            if (!strict) {
                if (!PluginCompareUtils.compareNode(this, expected, element, context.peek(), context, result)) {
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Nodes do not match."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
                }
            } else {
                Object tmp = ((JavascriptExecutor) client).executeScript("return arguments[0].innerHTML", element);
                IBuilderFactory bf = SRServices.get(IBuilderFactory.class);
                Builder builder = bf.newBuilder(new HashMap<String, Object>());
                Element received = (Element) builder.build("<html><head></head><body>" + String.valueOf(tmp) + "</body></html>", null).query("//body").get(0);
                INodeHolder holder = SRServices.get(INodeHolderFactory.class).newHolder(context.getNode());
                IComparator holderComparator = holder.getComparator(comparator.get());
                holderComparator.initialize();
                if (!holderComparator.match(expected, received)) {
                    IStringNormalizer sn = UtilString.getNormalizer();
                    result.addResult(Failure.INSTANCE, context.peek(), new DefaultAlignmentException(sn.normalize(UtilNode.getChildrenAsString(expected)), sn.normalize(UtilNode.getChildrenAsString(received))));
                } else {
                    result.addResult(Success.INSTANCE, context.peek());
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
    }

    /**
     * Check if the element stand for a node.
     * 
     * @param holder
     *            The element.
     * @return true, if is node comparison, false, otherwise.
     */
    public static boolean isNode(INodeHolder holder) {
        return holder.attributeEquals("type", "node");
    }
}
