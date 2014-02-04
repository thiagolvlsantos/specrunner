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
package org.specrunner.plugins.impl.flow;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.runner.RunnerException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilXPath;

/**
 * Create a condition execution.
 * <p>
 * Example of condition: <br>
 * 
 * 
 * <pre>
 *  Consider x is positive the message is "Positive" 
 *      else something different happens and the message is "Negative".
 * </pre>
 * 
 * <p>
 * With CSS annotations could be:
 * <p>
 * 
 * <pre>
 *  &lt;span class="if"&gt;
 *      Consider &lt;span class="test" value="${x > 0}"&gt;x is positive&lt;span&gt; 
 *      the message is "&lt;span class="then"&gt;Positive&lt;span&gt;" 
 *      else something different happens and the message is "&lt;span class="else"&gt;Negative&lt;span&gt;"
 *  &lt;span&gt;
 * </pre>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginIf extends AbstractPluginValue {

    public static final String CSS_TEST = "test";
    public static final String CSS_THEN = "then";
    public static final String CSS_ELSE = "else";
    public static final String CSS_SELECTED = "selected";
    public static final String CSS_RELEGATED = "relegated";

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Element node = (Element) context.getNode();
        Nodes conditions = node.query("descendant::*[@class='" + CSS_TEST + "']");
        if (conditions.size() == 0) {
            throw new PluginException("If without condition. Use i.e. a span with class='test'.");
        }
        Node condition = UtilXPath.getHighest(conditions);
        try {
            context.getRunner().run(condition, context, result);
        } catch (RunnerException e) {
            throw new PluginException("Condition could not be calculated.");
        }
        String strCond = ((Element) condition).getAttributeValue("value");
        Object valueCond = getValue(strCond != null ? strCond : condition.getValue(), true, context);
        valueCond = valueCond != null ? valueCond.toString() : "null";
        if (!("true".equalsIgnoreCase(valueCond.toString()) || "false".equalsIgnoreCase(valueCond.toString()))) {
            throw new PluginException("If contition result in invalid value: " + valueCond);
        }
        try {
            Node selected = null;
            Node relegated = null;
            Nodes thens = node.query("descendant::*[@class='" + CSS_THEN + "']");
            Nodes elses = node.query("descendant::*[@class='" + CSS_ELSE + "']");
            if (Boolean.TRUE.equals(Boolean.parseBoolean(valueCond.toString()))) {
                selected = UtilXPath.getHighest(thens);
                if (elses.size() > 0) {
                    relegated = UtilXPath.getHighest(elses);
                }
            } else {
                if (thens.size() > 0) {
                    relegated = UtilXPath.getHighest(thens);
                }
                selected = UtilXPath.getHighest(elses);
            }

            if (selected != null) {
                encapsulate(selected, true);
            }
            if (relegated != null) {
                encapsulate(relegated, false);
            }
            if (selected == null) {
                throw new PluginException("If without valid branch. " + node.toXML());
            } else {
                context.getRunner().run(selected, context, result);
            }
        } catch (RunnerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return ENext.SKIP;
    }

    public void encapsulate(Node node, boolean expanded) {
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        Element enc = new Element("span");
        enc.addAttribute(new Attribute("class", expanded ? CSS_SELECTED : CSS_RELEGATED));
        node.detach();
        if (expanded) {
            enc.appendChild(node);
            parent.insertChild(enc, index++);
        } else {
            boolean hide = Boolean.parseBoolean(((Element) node).getAttributeValue("hide"));
            if (!hide) {
                enc.appendChild(node);
                parent.insertChild(enc, index++);
            }
        }
    }
}
