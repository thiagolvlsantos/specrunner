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
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginLanguage extends AbstractPlugin {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
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
        Method m = getMethod(target, method, arguments);
        try {
            prepareArguments(context, m, arguments);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("TYPED ARGS:" + arguments);
            }
            m.invoke(target, arguments.toArray());
            result.addResult(Success.INSTANCE, context.peek());
        } catch (IllegalArgumentException e) {
            error = e.getCause();
        } catch (IllegalAccessException e) {
            error = e.getCause();
        } catch (InvocationTargetException e) {
            error = e.getCause();
        } catch (PluginException e) {
            error = e;
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
    protected abstract Method getMethod(Object target, String method, List<Object> args) throws PluginException;

    /**
     * Prepare argument list, based on parameter types.
     * 
     * @param context
     *            The context.
     * @param m
     *            The method.
     * @param args
     *            The arguments.
     * @throws PluginException
     *             On preparation errors.
     */
    protected abstract void prepareArguments(IContext context, Method m, List<Object> args) throws PluginException;

}
