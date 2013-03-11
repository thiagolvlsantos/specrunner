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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.runner.RunnerException;
import org.specrunner.util.UtilLog;

/**
 * Performs a indexed loop.
 * 
 * <p>
 * Example:
 * <p>
 * <blockquote> Check the square and cubic roots from 0 to 10, with step 2:
 * <table border=1>
 * <tr>
 * <th>n</th>
 * <th>square</th>
 * <th>cubic</th>
 * </tr>
 * <tr>
 * <td>${index}</td>
 * <td>${index * index}</td>
 * <td>${index * index * index}</td>
 * </tr>
 * </table>
 * </blockquote>
 * <p>
 * The CSS annotated version: <blockquote> Check the square and cubic roots from
 * 0 to 10, with step 2:
 * 
 * <pre>
 * &lt;table border=1>
 * &lt;tr>
 * &lt;th>n&lt;/th>
 * &lt;th>square&lt;/th>
 * &lt;th>cubic&lt;/th>
 * &lt;/tr>
 * &lt;tbody class="for" min=0 max=10 step=2 var=any>
 * &lt;tr>
 * &lt;td>${any}&lt;/td>
 * &lt;td>${any * any}&lt;/td>
 * &lt;td>${any * any * any}&lt;/td>
 * &lt;/tr>
 * &lt;tbody>
 * &lt;/table>
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFor extends AbstractPlugin {

    /**
     * Index variable name.
     */
    protected String var = "index";
    /**
     * Minimum value.
     */
    protected int min = 0;
    /**
     * Maximum value.
     */
    protected int max;
    /**
     * Step.
     */
    protected int step = 1;

    /**
     * The index variable name. Default is 'index'.
     * 
     * @return The variable name.
     */
    public String getVar() {
        return var;
    }

    /**
     * Change the variable name.
     * 
     * @param var
     *            The variable.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Gets the starting index. Default is '0'.
     * 
     * @return The starting index.
     */
    public int getMin() {
        return min;
    }

    /**
     * Set the starting index.
     * 
     * @param min
     *            The starting.
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Gets the ending index.
     * 
     * @return The last valid index.
     */
    public int getMax() {
        return max;
    }

    /**
     * Set the last valid index.
     * 
     * @param max
     *            The new last index.
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Gets the index jumping step.
     * 
     * @return The step.
     */
    public int getStep() {
        return step;
    }

    /**
     * Change index jumping step.
     * 
     * @param step
     *            The step.
     */
    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("VAR(" + var + "),MIN(" + min + "),MAX(" + max + "),STEP(" + step + ")");
        }
        Node current = context.getNode();
        if (min > max) {
            result.addResult(Failure.INSTANCE, context.newBlock(current, this), new PluginException("IPlugin loop 'min'(" + min + ") cannot be greater than 'max'(" + max + ")"));
            return ENext.SKIP;
        }
        Node base = current.copy();
        while (current.getChildCount() > 0) {
            current.getChild(0).detach();
        }
        Element ele = (Element) current;
        ele.addAttribute(new Attribute("var", "" + var));
        ele.addAttribute(new Attribute("min", "" + min));
        ele.addAttribute(new Attribute("max", "" + max));
        ele.addAttribute(new Attribute("step", "" + step));
        for (int i = min; i < max; i += step) {
            Node dn = base.copy();
            while (dn.getChildCount() > 0) {
                Node child = dn.getChild(0);
                child.detach();
                ((ParentNode) current).appendChild(child);
                String varName = "${" + var + "}";
                context.saveLocal(varName, i);
                try {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("CHILD(" + i + ")>" + child.toXML());
                    }
                    try {
                        context.getRunner().run(child, context, result);
                    } catch (RunnerException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        result.addResult(Failure.INSTANCE, context.newBlock(current, this), e);
                    }
                } finally {
                    context.clearLocal(varName);
                }
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("BASE(" + i + ")>" + base.toXML());
            }
        }
        return ENext.SKIP;
    }
}