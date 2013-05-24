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
package org.specrunner.util;

import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.plugins.PluginException;

/**
 * Utility class for expressions.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilEvaluator {

    /**
     * Escape mark.
     */
    public static final char ESCAPE = '\\';
    /**
     * Start code delimiter.
     */
    public static final String START_CODE = "${";
    /**
     * End code delimiter.
     */
    public static final String END = "}";
    /**
     * Start code replacement.
     */
    public static final String START_DATA = "#{";

    /**
     * Hidden constructor.
     */
    private UtilEvaluator() {
    }

    /**
     * Given a name, returns its equivalent as variables inside
     * <code>IContext</code> object.
     * 
     * @param name
     *            The source name.
     * @return The corresponding variable.
     */
    public static String asVariable(String name) {
        return START_CODE + name + END;
    }

    /**
     * Evaluates the text and returns an object.
     * 
     * @param text
     *            The text expression.
     * @param context
     *            A context.
     * @return The result.
     * @throws PluginException
     *             On evaluation errors.
     */
    public static Object evaluate(String text, IContext context) throws PluginException {
        IExpression expression = null;
        Object result = text;
        int pos1 = text.indexOf(START_CODE);
        int pos2 = text.indexOf(END, pos1 + 2);
        if (pos1 < 0 && pos2 < 0 && !isId(text)) {
            try {
                expression = SpecRunnerServices.get(IExpressionFactory.class).create(text, context);
                result = expression.evaluate(context);
            } catch (SpecRunnerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        while (pos1 >= 0 & pos2 > pos1) {
            // escape character
            if (pos1 > 0 && text.charAt(pos1 - 1) == ESCAPE) {
                pos1 = text.indexOf(START_CODE, pos2 + 1);
                pos2 = text.indexOf(END, pos1 + 2);
                continue;
            }
            String content = text.substring(pos1 + 2, pos2);
            String name = START_CODE + content + END;
            try {
                expression = SpecRunnerServices.get(IExpressionFactory.class).create(content, context);
                Object local = expression.evaluate(context);
                if (local != null) {
                    result = replace(String.valueOf(result), name, local);
                }
            } catch (SpecRunnerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            pos1 = text.indexOf(START_CODE, pos2 + 1);
            pos2 = text.indexOf(END, pos1 + 2);
        }
        return result;
    }

    /**
     * Check if a string can be a id.
     * 
     * @param str
     *            The candidate.
     * @return true, if is id, false, otherwise.
     */
    public static boolean isId(String str) {
        boolean result = true;
        for (int i = 0; result && i < str.length(); i++) {
            result = result && Character.isJavaIdentifierStart(str.charAt(i));
        }
        return result && !str.equals("true") && !str.equals("false");
    }

    /**
     * Replace ids in text.
     * 
     * @param text
     *            The text to be replaced.
     * @param context
     *            A context.
     * @return The replaced text.
     * @throws PluginException
     *             On replacing errors.
     */
    public static String replace(String text, IContext context) throws PluginException {
        String result = text;
        int pos1 = text.indexOf(START_CODE);
        int pos2 = text.indexOf(END, pos1 + 2);
        while (pos1 >= 0 & pos2 > pos1) {
            // escape character
            if (pos1 > 0 && text.charAt(pos1 - 1) == ESCAPE) {
                pos1 = text.indexOf(START_CODE, pos2 + 1);
                pos2 = text.indexOf(END, pos1 + 2);
                continue;
            }
            String content = text.substring(pos1 + 2, pos2);
            try {
                IExpression expression = SpecRunnerServices.get(IExpressionFactory.class).create(content, context);
                Object local = expression.evaluate(context);
                if (local != null) {
                    String name = START_CODE + content + END;
                    result = replace(result, name, local);
                }
            } catch (SpecRunnerException e) {
                throw new PluginException(e);
            }
            pos1 = text.indexOf(START_CODE, pos2 + 1);
            pos2 = text.indexOf(END, pos1 + 2);
        }
        return result;
    }

    /**
     * Replace the same string which appear many times in text.
     * 
     * @param text
     *            The text to be replaced.
     * @param name
     *            The name.
     * @param local
     *            The replacing value.
     * @return The replaced string.
     */
    public static String replace(String text, String name, Object local) {
        StringBuilder sb = new StringBuilder();
        int pos1 = 0;
        int pos2 = text.indexOf(name);
        while (pos1 >= 0 && pos2 >= 0) {
            sb.append(text.substring(pos1, pos2));
            if (pos2 > 0 && text.charAt(pos2 - 1) == ESCAPE) {
                sb.append(name);
            } else {
                sb.append(String.valueOf(local));
            }
            pos1 = pos2 + name.length();
            pos2 = text.indexOf(name, pos1);
        }
        sb.append(text.substring(pos1, text.length()));
        return sb.toString();
    }

    /**
     * Replaces text with corresponding values in map.
     * 
     * @param text
     *            The text to be replace.
     * @param map
     *            The map of values to be replace.
     * @return The nodes which represents the replaced text.
     * @throws PluginException
     *             On replacement errors.
     */
    public static Nodes replaceMap(String text, Map<String, Node> map) throws PluginException {
        Nodes nodes = new Nodes();
        int pos1 = 0;
        int pos2 = text.indexOf(UtilEvaluator.START_DATA);
        int pos3 = text.indexOf(UtilEvaluator.END, pos2 + UtilEvaluator.START_DATA.length() + 1);
        while (pos2 >= 0 & pos3 > pos2) {
            nodes.append(new Text(text.substring(pos1, pos2)));
            String name = text.substring(pos2, pos3 + 1);
            Node n = map.get(name);
            if (n != null) {
                if (n instanceof ParentNode) {
                    ParentNode pn = (ParentNode) n;
                    for (int i = 0; i < pn.getChildCount(); i++) {
                        nodes.append(pn.getChild(i).copy());
                    }
                } else {
                    nodes.append(n.copy());
                }
            } else {
                nodes.append(new Text(text.substring(pos2, pos3)));
            }
            pos1 = pos3 + 1;
            pos2 = text.indexOf(UtilEvaluator.START_DATA, pos1);
            pos3 = text.indexOf(UtilEvaluator.END, pos2 + UtilEvaluator.START_DATA.length() + 1);
        }
        if (pos1 != text.length() + 1) {
            nodes.append(new Text(text.substring(pos1, text.length())));
        }
        return nodes;
    }
}