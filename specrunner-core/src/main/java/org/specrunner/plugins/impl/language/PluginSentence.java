package org.specrunner.plugins.impl.language;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.junit.ExpectedMessage;
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
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.xom.IElementHolder;
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
     * Flag to enable lookup method after arguments conversion.
     */
    private Boolean after = Boolean.FALSE;

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
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
        StringBuilder methodName = new StringBuilder();
        List<Object> arguments = new LinkedList<Object>();
        extractMethodNameArguments(context, methodName, arguments);
        String method = UtilString.camelCase(methodName.toString());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("FULL:" + context.getNode().toXML());
            UtilLog.LOG.debug("TEXT:" + methodName);
            UtilLog.LOG.debug("METH:" + method);
            UtilLog.LOG.debug("ARGS:" + arguments);
        }
        Throwable error = null;
        Method m = after ? getMethodAfter(target, method, arguments) : getMethodBefore(target, method, arguments);
        try {
            if (after) {
                prepareArgumentsAfter(context, m, arguments);
            } else {
                prepareArgumentsBefore(context, m, arguments);
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("TYPED ARGS:" + arguments);
            }
            m.invoke(target, arguments.toArray());
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
        if (error != null) {
            ExpectedMessage em = m.getAnnotation(ExpectedMessage.class);
            if (em == null) {
                throw new PluginException(error);
            }
            String received = error.getMessage();
            String expectation = em.message();
            if (expectation.equals(received)) {
                result.addResult(Success.INSTANCE, context.peek());
                return;
            }
            throw new PluginException("Unexpected message received: " + error.getMessage(), error);
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
     * @param text
     *            The text part.
     * @param args
     *            The argument objects.
     * @throws PluginException
     *             On errors.
     */
    protected void extractMethodNameArguments(IContext context, StringBuilder text, List<Object> args) throws PluginException {
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
                if (i > 0 && tmp.charAt(i - 1) == '\\') {
                    stack.peek().setLength(stack.peek().length() - 1);
                    stack.peek().append(c);
                    continue;
                }
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
     *            Possible arguments.
     * @return The corresponding method.
     * @throws PluginException
     *             On method lookup errors.
     * 
     */
    protected Method getMethodBefore(Object target, String method, List<Object> args) throws PluginException {
        Class<?> type = target.getClass();
        Method[] methods = type.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method) && m.getParameterTypes().length == args.size()) {
                return m;
            }
        }
        throw new PluginException("Method named '" + method + "' with " + args.size() + " parameter(s) not found for " + type);
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
        Class<?>[] types = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        IConverterManager cm = SpecRunnerServices.get(IConverterManager.class);
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            Object arg = arguments.get(i);
            if (!type.isInstance(arg)) {
                IConverter converter = null;
                Object[] converterArguments = null;
                Converter annotation = getConverter(parameterAnnotations[i]);
                if (annotation != null) {
                    String name = annotation.name();
                    if (!name.isEmpty()) {
                        converter = cm.get(name);
                        if (converter == null) {
                            throw new PluginException("Converter named '" + name + "' not found.");
                        }
                    } else {
                        try {
                            converter = annotation.type().newInstance();
                        } catch (InstantiationException e) {
                            throw new PluginException(e);
                        } catch (IllegalAccessException e) {
                            throw new PluginException(e);
                        }
                    }
                    Class<?> resultType = annotation.resultType();
                    if (resultType != Object.class) {
                        converterArguments = new Object[] { resultType };
                    } else {
                        converterArguments = annotation.args();
                    }
                } else {
                    converter = cm.get(type.getSimpleName().toLowerCase());
                    converterArguments = new Object[] {};
                }
                if (converter != null) {
                    try {
                        Object tmp = converter.convert(arg, converterArguments);
                        if (!type.isInstance(tmp)) {
                            throw new PluginException("Invalid parameter value for argument[" + i + "] in " + method + ". Expected " + type + ", received: " + tmp + " of type " + tmp.getClass());
                        }
                        arguments.remove(i);
                        arguments.add(i, tmp);
                    } catch (ConverterException e) {
                        throw new PluginException(e);
                    }
                }
            }
        }
    }

    /**
     * Extract converter information from parameter annotations.
     * 
     * @param annots
     *            The annotations.
     * @return The converter annotation if exists, null, otherwise.
     */
    protected Converter getConverter(Annotation[] annots) {
        Converter conv = null;
        for (int j = 0; j < annots.length; j++) {
            if (annots[j] instanceof Converter) {
                conv = (Converter) annots[j];
            }
        }
        return conv;
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
