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
package org.specrunner.plugins.core.flow;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

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
 *  Consider &lt;span class="if" value="${x > 0}"&gt;x is positive&lt;span&gt; 
 *      the message is "&lt;span class="then"&gt;Positive&lt;span&gt;" 
 *      else something different happens and the message is "&lt;span class="else"&gt;Negative&lt;span&gt;".
 * </pre>
 * 
 * With complex descriptions, the order can be different and the group of
 * 'if'/'then'/'else' can be named to avoid interferences of others
 * if/then/else.
 * <p>
 * Example1:
 * 
 * <pre>
 *  Consider &lt;span class="if" value="${x > 0}" name="test1"&gt;x is positive&lt;span&gt; 
 *      the message is "&lt;span class="then" name="test1"&gt;Positive&lt;span&gt;" 
 *      else something different happens and the message is "&lt;span class="else" name="test1"&gt;Negative&lt;span&gt;".
 * </pre>
 * 
 * Example2:
 * 
 * <pre>
 *  Consider &lt;span class="if" value="${x > 0}" &gt;x is positive&lt;span&gt; 
 *  Consider &lt;span class="if" value="${y > 0}" name="testY"&gt;y is negative&lt;span&gt;
 *      
 *  the message for x is "&lt;span class="then" name="test1"&gt;Positive&lt;span&gt;" 
 *  the message for y is "&lt;span class="then" name="testY"&gt;Positive&lt;span&gt;" 
 *  else something different happens and the message for x is "&lt;span class="else"&gt;Negative&lt;span&gt;".
 *  else something different happens and the message for y is "&lt;span class="else" name="testY"&gt;Negative&lt;span&gt;".
 * </pre>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginIf extends AbstractPluginValue {

    /**
     * The conditional default name. To use another name use 'name' attribute.
     */
    public static final String TEST_NAME = "ifTest";

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Element condition = (Element) context.getNode();
        INodeHolder nh = SRServices.get(INodeHolderFactory.class).newHolder(context.getNode());
        Object valueCond = nh.getObject(context, true);
        if (!(valueCond instanceof Boolean)) {
            String strCon = String.valueOf(valueCond);
            if ("true".equalsIgnoreCase(strCon) || "false".equalsIgnoreCase(strCon)) {
                valueCond = Boolean.valueOf(strCon);
            } else {
                throw new PluginException("If contition result in invalid value: '" + valueCond + "'. Returned type:" + (valueCond != null ? valueCond.getClass() : "null"));
            }
        }
        String testName = getName() != null ? getName() : TEST_NAME;
        condition.addAttribute(new Attribute("name", testName));
        condition.addAttribute(new Attribute("branch", String.valueOf(valueCond)));
        saveLocal(context, testName, valueCond);
        return ENext.DEEP;
    }

    /**
     * Gets the previously calculated condition with the given name.
     * 
     * @param context
     *            The context.
     * @param name
     *            The test name.
     * @return true, if test passed, false, otherwise.
     * @throws PluginException
     *             On lookup errors.
     */
    public static Boolean getTest(IContext context, String name) throws PluginException {
        String ifName = name != null ? name : TEST_NAME;
        Object ifObject = context.getByName(ifName);
        if (ifObject == null) {
            throw new PluginException("Conditional test with name '" + ifName + "' not found in context.");
        }
        if (!(ifObject instanceof Boolean)) {
            throw new PluginException("Conditional test with name '" + ifName + "' returned a '" + (ifObject != null ? ifObject.getClass().getName() : "null") + "' instance of an instance of Boolean.");
        }
        Node node = context.getNode();
        if (node instanceof Element) {
            ((Element) node).addAttribute(new Attribute("name", "" + ifName));
        }
        return ((Boolean) ifObject);
    }

}
