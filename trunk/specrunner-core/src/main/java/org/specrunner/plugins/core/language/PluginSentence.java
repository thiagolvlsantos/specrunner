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
package org.specrunner.plugins.core.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.converters.UtilConverter;
import org.specrunner.junit.ExpectedMessage;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.core.elements.PluginHtml;
import org.specrunner.plugins.core.var.PluginBean;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.plugins.type.Command;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * A natural language plugin to perform pattern matching like
 * JBehave/Cucumber/Twist fixtures.
 * 
 * Method match by default is performed from Java to arguments, the other
 * direction is possible using <code>after=true</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSentence extends AbstractPlugin {

    /**
     * The plugin type.
     */
    private ActionType type = Undefined.INSTANCE;

    /**
     * Method to call.
     */
    private String method;

    /**
     * Flag to enable lookup method after arguments conversion.
     */
    private Boolean after = Boolean.FALSE;

    /**
     * Cache of type to methods annotated with sentence.
     */
    protected static ICache<Class<?>, List<Method>> cacheMethods = SRServices.get(ICacheFactory.class).newCache(PluginSentence.class.getName() + "_methods");

    /**
     * Cache of patterns.
     */
    protected static ICache<String, Pattern> cachePatterns = SRServices.get(ICacheFactory.class).newCache(PluginSentence.class.getName() + "_patterns");

    @Override
    public ActionType getActionType() {
        return type;
    }

    /**
     * Get method name.
     * 
     * @return The method name.
     */
    public String getMethod() {
        return method;
    }

    /**
     * The method to call to this sentence.
     * 
     * @param method
     *            The method name.
     */
    @DontEval
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * The flag status.
     * 
     * @return true, to perform method lookup after arguments conversion, false,
     *         to perform method.
     */
    public Boolean getAfter() {
        return after;
    }

    /**
     * The flag to perform method lookup after arguments conversion.
     * 
     * @param after
     *            true, to lookup method after arguments conversion, false,
     *            otherwise.
     */
    public void setAfter(Boolean after) {
        this.after = after;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        Object target = getObject(context);
        if (target == null) {
            throw new PluginException("Target object cannot be null.");
        }
        String methodToCall = method;
        StringBuilder methodName = new StringBuilder();
        List<Object> arguments = new LinkedList<Object>();
        extractMethodNameArguments(context, target, methodName, arguments);
        if (methodToCall == null || methodToCall.isEmpty()) {
            methodToCall = methodName.toString();
        }
        if (methodToCall == null || methodToCall.isEmpty()) {
            throw new PluginException("Method to call not found. Add a 'method' attribute, or extend text to match a regular expression in @Sentence annotations.");
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("FULL:" + context.getNode().toXML());
            UtilLog.LOG.debug("TEXT:" + methodName);
            UtilLog.LOG.debug("METH:" + methodToCall);
            UtilLog.LOG.debug("ARGS:" + arguments);
        }
        Throwable error = null;
        Method m = after ? getMethodAfter(target, methodToCall, arguments) : getMethodBefore(target, methodToCall, arguments);
        if (m.getReturnType() == Boolean.class || m.getReturnType() == boolean.class) {
            type = Assertion.INSTANCE;
        } else {
            type = Command.INSTANCE;
        }
        try {
            if (after) {
                prepareArgumentsAfter(context, m, arguments);
            } else {
                prepareArgumentsBefore(context, m, arguments);
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("TYPED ARGS[" + arguments.size() + "]:");
                for (int i = 0; i < arguments.size(); i++) {
                    Object tmp = arguments.get(i);
                    UtilLog.LOG.debug("\t ARGS(" + i + "):" + tmp + " of type '" + (tmp != null ? tmp.getClass() : "null") + "'.");
                }
            }
            StringBuilder sb = new StringBuilder();
            Class<?>[] expectedTypes = m.getParameterTypes();
            for (int i = 0; i < expectedTypes.length; i++) {
                Object arg = arguments.get(i);
                if (!UtilConverter.getWrapper(expectedTypes[i]).isInstance(arg)) {
                    sb.append("Method '" + m + "' expected argument[" + i + "] of type '" + expectedTypes[i].getName() + "' received '" + arg + "' of type '" + (arg == null ? "null" : arg.getClass().getName()) + "'.\n");
                }
            }
            if (sb.length() != 0) {
                sb.setLength(sb.length() - 1);
                throw new PluginException(sb.toString());
            }
            Object tmp = m.invoke(target, arguments.toArray());
            if (type == Assertion.INSTANCE) {
                if (tmp instanceof Boolean && !((Boolean) tmp)) {
                    throw new PluginException("Expected result of '" + m + "' must be 'true'. Received 'false'.");
                }
            }
            result.addResult(Success.INSTANCE, context.peek());
        } catch (IllegalArgumentException e) {
            error = e.getCause() != null ? e.getCause() : e;
        } catch (IllegalAccessException e) {
            error = e.getCause() != null ? e.getCause() : e;
        } catch (InvocationTargetException e) {
            error = e.getCause() != null ? e.getCause() : e;
        } catch (PluginException e) {
            error = e;
        }
        ExpectedMessage em = m.getAnnotation(ExpectedMessage.class);
        if (error != null) {
            if (em == null) {
                error = UtilException.unwrapException(error);
                result.addResult(Failure.INSTANCE, context.peek(), error);
                return;
            }
            String received = error.getMessage();
            String expectation = em.value();
            if (expectation.equals(received)) {
                result.addResult(Success.INSTANCE, context.peek());
                return;
            }
            result.addResult(Failure.INSTANCE, context.peek(), new DefaultAlignmentException("" + m + arguments + "\nExpected message does not match.", expectation, received));
        } else {
            if (em != null) {
                result.addResult(Failure.INSTANCE, context.peek(), "Expected message not received.\nMessage: " + em.value());
            }
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

    /**
     * Extract text and arguments.
     * 
     * @param context
     *            The context.
     * @param target
     *            The target object.
     * @param methodName
     *            The text part.
     * @param arguments
     *            The argument objects.
     * @throws PluginException
     *             On errors.
     */
    protected void extractMethodNameArguments(IContext context, Object target, StringBuilder methodName, List<Object> arguments) throws PluginException {
        Node node = context.getNode();
        boolean onlyText = true;
        StringBuilder startText = null;
        for (int i = 0; onlyText && i < node.getChildCount(); i++) {
            Node child = node.getChild(i);
            onlyText = child instanceof Text;
            if (onlyText) {
                if (startText == null) {
                    startText = new StringBuilder();
                }
                startText.append(child.getValue());
            }
        }
        INodeHolder holder = UtilNode.newNodeHolder(onlyText ? node : new Text(startText.toString()));
        String value = String.valueOf(holder.getObject(context, true));
        boolean annotation = fromAnnotations(value, target, methodName, arguments);
        if (!onlyText || annotation) {
            onlyArgs(context, node, annotation ? new StringBuilder() : methodName, arguments);
        } else {
            onlyText(holder.getValue(), methodName, arguments);
        }
        if (!annotation) {
            String tmp = UtilString.camelCase(methodName.toString());
            methodName.setLength(0);
            methodName.append(tmp);
        }
    }

    /**
     * Perform method search from annotations.
     * 
     * @param value
     *            The node text value.
     * @param target
     *            The target object.
     * @param text
     *            The text.
     * @param args
     *            The arguments.
     * @return true, if some method annotation matches the value, false,
     *         otherwise.
     * @throws PluginException
     *             On invalid placeholder replacement.
     */
    protected boolean fromAnnotations(String value, Object target, StringBuilder text, List<Object> args) throws PluginException {
        // when a method is specified regular expressions are useless.
        if (method != null) {
            return false;
        }
        Class<?> clazz = target.getClass();
        List<Method> ms = null;
        synchronized (cacheMethods) {
            ms = cacheMethods.get(clazz);
            if (ms == null) {
                ms = new LinkedList<Method>();
                for (Method m : clazz.getMethods()) {
                    Sentence s = m.getAnnotation(Sentence.class);
                    if (s != null) {
                        ms.add(m);
                    }
                }
                cacheMethods.put(clazz, ms);
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Class " + clazz + " mapped to @Sentence annotated methods: '" + ms + "'.");
                }
            } else {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Class " + clazz + " map to @Sentence reused.");
                }
            }
        }
        for (Method m : ms) {
            Sentence s = m.getAnnotation(Sentence.class);
            List<String> strs = new LinkedList<String>();
            strs.add(s.value());
            Synonyms sm = m.getAnnotation(Synonyms.class);
            if (sm != null) {
                for (String syn : sm.value()) {
                    strs.add(syn);
                }
            }
            boolean found = false;
            for (int i = 0; i < strs.size(); i++) {
                String str = strs.get(i);
                str = removePlaceholders(m, str);
                Pattern pattern = null;
                synchronized (cachePatterns) {
                    pattern = cachePatterns.get(str);
                    if (pattern == null) {
                        pattern = Pattern.compile(str, i == 0 || sm == null ? s.options() : sm.options());
                        cachePatterns.put(str, pattern);
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace("New pattern for '" + str + "' created.");
                        }
                    } else {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace("Reused pattern for '" + str + "'.");
                        }
                    }
                }
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    for (int j = 1; j <= matcher.groupCount(); j++) {
                        args.add(matcher.group(j));
                    }
                    text.append(m.getName());
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        return text.length() != 0;
    }

    /**
     * Remove place holders registered in <code>Placeholders</code>. i.e. $int
     * will be replaced to '(\\d+)'.
     * 
     * @param method
     *            The method.
     * @param str
     *            The string to be replaced.
     * @return The replaced string.
     * @throws PluginException
     *             On invalid placeholder replacement.
     */
    protected String removePlaceholders(Method method, String str) throws PluginException {
        Class<?>[] types = method.getParameterTypes();
        int index = 0;
        String result = str;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '$') {
                StringBuilder tmp = new StringBuilder("$");
                while (++i < str.length() && str.charAt(i) != ' ') {
                    tmp.append(str.charAt(i));
                }
                String key = tmp.toString();
                String placeholder = Placeholders.get().get(key);
                if (placeholder == null) {
                    String replacement = "$" + types[index].getSimpleName().toLowerCase();
                    placeholder = Placeholders.get().get(replacement);
                    if (placeholder == null) {
                        throw new PluginException("On placeholder '" + key + "' resolution, none pattern found for '" + replacement + "'.");
                    }
                    result = result.replace(key, placeholder);
                }
                result = result.replace(key, placeholder);
                index++;
            }
        }
        return result;
    }

    /**
     * Extract parameters from text. In this approach, all parameters are
     * expected as strings delimited by quotes.
     * 
     * @param text
     *            The text.
     * @param methodName
     *            The method name.
     * @param arguments
     *            The arguments.
     */
    protected void onlyText(String text, StringBuilder methodName, List<Object> arguments) {
        Stack<StringBuilder> stack = new Stack<StringBuilder>();
        stack.push(new StringBuilder());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                if (i > 0 && text.charAt(i - 1) == '\\') {
                    stack.peek().setLength(stack.peek().length() - 1);
                    stack.peek().append(c);
                    continue;
                }
                if (stack.size() == 1) {
                    stack.push(new StringBuilder());
                } else {
                    arguments.add(stack.pop().toString());
                }
                continue;
            }
            stack.peek().append(c);
        }
        methodName.append(stack.pop().toString());
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
                INodeHolder h = UtilNode.newNodeHolder(n);
                if (h.hasName("arg") || h.attributeContains("class", "arg")) {
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
     *            Possible arguments.
     * @return The corresponding method.
     * @throws PluginException
     *             On method lookup errors.
     * 
     */
    protected Method getMethodBefore(Object target, String method, List<Object> args) throws PluginException {
        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method) && m.getParameterTypes().length == args.size()) {
                return m;
            }
        }
        throw new PluginException("Method named '" + method + "' with " + args.size() + " parameter(s) not found for " + clazz + ".\n Another reason to this error show up is when you have defined a wrong regular expression for the target method you expected to call [Check @Sentence and @Synonyms annotations].");
    }

    /**
     * Prepare argument list, based on parameter types.
     * 
     * @param context
     *            The context.
     * @param method
     *            The method.
     * @param arguments
     *            The arguments.
     * @throws PluginException
     *             On preparation errors.
     */
    protected void prepareArgumentsBefore(IContext context, Method method, List<Object> arguments) throws PluginException {
        UtilConverter.prepareMethodArguments(method, arguments);
    }

    /**
     * Get object method.
     * 
     * @param target
     *            The object target.
     * @param method
     *            The method name.
     * @param args
     *            Possible arguments.
     * @return The corresponding method.
     * @throws PluginException
     *             On method lookup errors.
     * 
     */
    protected Method getMethodAfter(Object target, String method, List<Object> args) throws PluginException {
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
     * Prepare argument list, based on parameter types.
     * 
     * @param context
     *            The context.
     * @param method
     *            The method.
     * @param arguments
     *            The arguments.
     * @throws PluginException
     *             On preparation errors.
     */
    protected void prepareArgumentsAfter(IContext context, Method method, List<Object> arguments) throws PluginException {
        // nothing to change.
    }
}