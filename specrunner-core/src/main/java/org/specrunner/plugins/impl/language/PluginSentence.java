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
package org.specrunner.plugins.impl.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.impl.var.PluginBean;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.IElementHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * A natural language plugin to perform pattern matching like
 * JBehave/Cucumber/Twist fixtures.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginSentence extends AbstractPlugin {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        StringBuilder text = new StringBuilder();
        List<Object> args = new LinkedList<Object>();
        extractTextArgs(context, text, args);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("FULL:" + context.getNode().toXML());
            UtilLog.LOG.debug("TEXT:" + text);
            UtilLog.LOG.debug("ARGS:" + args);
        }
        String method = UtilString.camelCase(text.toString());
        Object target = getObject(context);
        Method m = getMethod(target, method, args);
        Throwable error = null;
        try {
            m.invoke(target, args.toArray());
            result.addResult(Success.INSTANCE, context.peek());
        } catch (IllegalArgumentException e) {
            error = e.getCause();
        } catch (IllegalAccessException e) {
            error = e.getCause();
        } catch (InvocationTargetException e) {
            error = e.getCause();
        }
        if (error != null) {
            ExpectedMessages em = m.getAnnotation(ExpectedMessages.class);
            if (em == null) {
                throw new PluginException(error);
            }
            String received = error.getMessage();
            String[] expectations = em.messages();
            for (int i = 0; i < expectations.length; i++) {
                if (expectations[i].equals(received)) {
                    result.addResult(Success.INSTANCE, context.peek());
                    return;
                }
            }
            throw new PluginException("Unexpected message received: " + error.getMessage());
        }
    }

    /**
     * Extract text and arguments.
     * 
     * @param context
     *            The context.
     * @param text
     *            The text part.
     * @param args
     *            The argument objects.
     * @throws PluginException
     *             On errors.
     */
    protected void extractTextArgs(IContext context, StringBuilder text, List<Object> args) throws PluginException {
        Node node = context.getNode();
        if (node.getChildCount() == 1 && node.getChild(0) instanceof Text) {
            onlyText(context, node, text, args);
        } else {
            onlyArgs(context, node, text, args);
        }
    }

    /**
     * Extract parameters from text. In this approach, all parameters are
     * expected as strings.
     * 
     * @param context
     *            The context.
     * @param node
     *            The current node.
     * @param text
     *            The text.
     * @param args
     *            The arguments.
     * @throws PluginException
     *             On errors.
     */
    protected void onlyText(IContext context, Node node, StringBuilder text, List<Object> args) throws PluginException {
        IElementHolder holder = UtilNode.newElementAdapter(node);
        String tmp = String.valueOf(holder.getObject(context, true));
        Stack<StringBuilder> stack = new Stack<StringBuilder>();
        stack.push(new StringBuilder());
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (c == '"') {
                if (stack.size() == 1) {
                    stack.push(new StringBuilder());
                } else {
                    args.add(stack.pop().toString());
                }
                continue;
            }
            stack.peek().append(c);
        }
        text.append(stack.pop().toString());
    }

    /**
     * Extract arguments from nodes.
     * 
     * @param context
     *            The context.
     * @param node
     *            The node.
     * @param text
     *            The text.
     * @param args
     *            The arguments.
     * @throws PluginException
     *             On errors.
     */
    protected void onlyArgs(IContext context, Node node, StringBuilder text, List<Object> args) throws PluginException {
        if (node instanceof Text) {
            text.append(node.getValue());
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            Node n = node.getChild(i);
            if (n instanceof Element) {
                IElementHolder h = UtilNode.newElementAdapter(n);
                if ("arg".equals(h.getLocalName()) || (h.hasAttribute("class") && h.getAttribute("class").contains("arg"))) {
                    args.add(h.getObject(context, true));
                } else {
                    onlyArgs(context, n, text, args);
                }
            } else {
                onlyArgs(context, n, text, args);
            }
        }
    }

    /**
     * Get object method.
     * 
     * @param target
     *            The object target.
     * @param method
     *            The method name.
     * @param args
     *            Arguments.
     * @return The corresponding method.
     * @throws PluginException
     *             On method lookup errors.
     * 
     */
    protected Method getMethod(Object target, String method, List<Object> args) throws PluginException {
        if (target == null) {
            throw new PluginException("Target object cannot be null.");
        }
        Class<?> type = target.getClass();
        Class<?>[] types = new Class<?>[args.size()];
        for (int i = 0; i < args.size(); i++) {
            Object tmp = args.get(i);
            types[i] = tmp != null ? tmp.getClass() : Object.class;
        }
        try {
            return type.getMethod(method, types);
        } catch (SecurityException e) {
            throw new PluginException(e);
        } catch (NoSuchMethodException e) {
            throw new PluginException(e);
        }
    }

    /**
     * Get the object instance to be used by plugin actions.
     * 
     * @param context
     *            The context.
     * @return An object instance where all actions will taken over.
     */
    protected Object getObject(IContext context) {
        Object instance = PluginBean.getBean(context);
        if (instance == null) {
            instance = PluginHtml.getTestInstance();
        }
        return instance;
    }

}