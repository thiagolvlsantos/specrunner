package org.specrunner.plugins.impl.language;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.impl.var.PluginBean;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.IElementHolder;
import org.specrunner.util.xom.UtilNode;

public class PluginSentence extends AbstractPlugin {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        IElementHolder holder = UtilNode.newElementAdapter(context.getNode());
        /*
         * TODO: 1) FILTRA O TEXTO QUE VIRA MÉTODO; 2) EXTRAIR OS PARÂMETROS: A)
         * COMO STRING "<PARAMETRO>"; (OK) B) COMO <ARG>...</ARG> OU
         * <..CLASS="ARG">...</ARG>; C) MISTURANDO AMBOS.
         */

        String text = String.valueOf(holder.getObject(context, true));
        Stack<StringBuilder> parts = new Stack<StringBuilder>();
        parts.push(new StringBuilder());
        List<Object> args = new LinkedList<Object>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                if (parts.size() > 1) {
                    args.add(parts.pop().toString());
                } else {
                    parts.push(new StringBuilder());
                }
                continue;
            } else {
                parts.peek().append(c);
            }
        }
        String method = UtilString.camelCase(parts.peek().toString());
        Object target = getObjectInstance(context);
        Method m = getMethod(target, method, args);
        try {
            m.invoke(target, args.toArray());
        } catch (IllegalArgumentException e) {
            throw new PluginException(e);
        } catch (IllegalAccessException e) {
            throw new PluginException(e);
        } catch (InvocationTargetException e) {
            throw new PluginException(e);
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
    protected Object getObjectInstance(IContext context) {
        Object instance = PluginBean.getBean(context);
        if (instance == null) {
            instance = PluginHtml.getTestInstance();
        }
        return instance;
    }

}