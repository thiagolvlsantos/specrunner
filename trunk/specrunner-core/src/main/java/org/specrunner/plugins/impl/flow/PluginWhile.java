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
package org.specrunner.plugins.impl.flow;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.runner.RunnerException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Performs a conditional loop.
 * 
 * <p>
 * Example:
 * <p>
 * <blockquote> While the number of elements x (initially 10) is bigger than 5
 * (x&gt;5), calculate:
 * <table border=1>
 * <tr>
 * <th>x</th>
 * <th>square</th>
 * <th>cubic</th>
 * </tr>
 * <tr>
 * <td>${x}</td>
 * <td>${x * x}</td>
 * <td>${x * x * x}</td>
 * </tr>
 * </table>
 * </blockquote>
 * <p>
 * The CSS annotated version: <blockquote>
 * 
 * <pre>
 * &lt;div>
 * While the number of elements &lt;span class="local" name="x" value="10">x (initially 10)&lt;/span> is bigger than 5 (x&gt;5), calculate:
 * &lt;table border=1>
 * &lt;tr>
 * &lt;th>x&lt;/th>
 * &lt;th>square&lt;/th>
 * &lt;th>cubic&lt;/th>
 * &lt;/tr>
 * &lt;tbody class="while" test="x>5">
 * &lt;tr >
 * &lt;td>${x}&lt;/td>
 * &lt;td>${x * x}&lt;/td>
 * &lt;td>${x * x * x}&lt;/td>
 * &lt;/tr>
 * &lt;local scope="div" name="x" value="x-1"/>
 * &lt;tbody>
 * &lt;/table>
 * &lt;/div>
 * </pre>
 * 
 * </blockquote> Alternative version using "condition". * The CSS annotated
 * version: <blockquote>
 * 
 * <pre>
 * &lt;div>
 * While the number of elements &lt;span class="local" name="x" value="10">x (initially 10)&lt;/span> is bigger than 5 (&lt;span class="condition" name="cond1">x&gt;5&lt;/span>), calculate:
 * &lt;table border=1>
 * &lt;tr>
 * &lt;th>x&lt;/th>
 * &lt;th>square&lt;/th>
 * &lt;th>cubic&lt;/th>
 * &lt;/tr>
 * &lt;tbody class="while" name="cond1">
 * &lt;tr >
 * &lt;td>${x}&lt;/td>
 * &lt;td>${x * x}&lt;/td>
 * &lt;td>${x * x * x}&lt;/td>
 * &lt;/tr>
 * &lt;local scope="div" name="x" value="x-1"/>
 * &lt;tbody>
 * &lt;/table>
 * &lt;/div>
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * @author Thiago Santos
 * 
 */
public class PluginWhile extends AbstractPluginValue {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node current = context.getNode();
        Element ele = (Element) current;
        String test = ele.getAttributeValue("test");
        if (test == null) {
            test = (String) context.getByName(UtilEvaluator.asVariable(ele.getAttributeValue("name")));
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("WHILE(" + test + ")");
        }
        if (test == null) {
            result.addResult(Failure.INSTANCE, context.newBlock(current, this), new PluginException("IPlugin while missing 'test' condition or a name which reffers to a previously defined condition."));
            return ENext.SKIP;
        }
        Node base = current.copy();
        while (current.getChildCount() > 0) {
            current.getChild(0).detach();
        }
        while (checkCondition(test, context)) {
            Node dn = base.copy();
            while (dn.getChildCount() > 0) {
                Node child = dn.getChild(0);
                child.detach();
                ((ParentNode) current).appendChild(child);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("CHILD()>" + child.toXML());
                }
                try {
                    context.getRunner().run(child, context, result);
                } catch (RunnerException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Failure.INSTANCE, context.newBlock(current, this), e);
                }
            }
        }
        return ENext.SKIP;
    }

    /**
     * Check condition.
     * 
     * @param test
     *            The condition.
     * @param context
     *            The context.
     * @return true, if condition is valid, false, otherwise.
     * @throws PluginException
     *             On evaluation errors.
     */
    protected boolean checkCondition(String test, IContext context) throws PluginException {
        Object tmp = UtilEvaluator.evaluate(test, context);
        return tmp instanceof Boolean && (Boolean) tmp;
    }
}