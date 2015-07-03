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
package org.specrunner.converters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Converter utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilConverter {

    /**
     * Map of primitive types for correct use of
     * <code>&lt;type&gt;.isInstance(...)</code>.
     */
    public static final Map<Class<?>, Class<?>> PRIMITIVES = new HashMap<Class<?>, Class<?>>();

    /**
     * Default constructor.
     */
    private UtilConverter() {
    }

    static {
        PRIMITIVES.put(boolean.class, Boolean.class);
        PRIMITIVES.put(char.class, Character.class);
        PRIMITIVES.put(byte.class, Byte.class);
        PRIMITIVES.put(short.class, Short.class);
        PRIMITIVES.put(int.class, Integer.class);
        PRIMITIVES.put(long.class, Long.class);
        PRIMITIVES.put(float.class, Float.class);
        PRIMITIVES.put(double.class, Double.class);
    }

    /**
     * Get errors.
     * 
     * @param clazz
     *            O errors.
     * @return The class.
     */
    public static Class<?> getWrapper(Class<?> clazz) {
        Class<?> primitive = PRIMITIVES.get(clazz);
        return primitive == null ? clazz : primitive;
    }

    /**
     * Prepare arguments.
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
    public static void prepareMethodArguments(IContext context, Method method, List<Object> arguments) throws PluginException {
        Class<?>[] types = method.getParameterTypes();
        boolean isVarArgs = method.getParameterTypes().length == 1 && method.isVarArgs();
        if (!isVarArgs && types.length != arguments.size()) {
            throw new PluginException(method + " has different number of arguments. Received arguments:" + arguments);
        }
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; (!isVarArgs && i < types.length) || (isVarArgs && i < arguments.size()); i++) {
            arguments.add(i, prepareArgument(context, method.toString(), isVarArgs ? parameterAnnotations[0] : parameterAnnotations[i], isVarArgs ? types[0] : types[i], arguments.get(i)));
            arguments.remove(i + 1);
        }
        if (isVarArgs) {
            List<Object> group = new LinkedList<Object>(arguments);
            arguments.clear();
            if (!group.isEmpty()) {
                Class<?> clazz = group.get(0).getClass();
                arguments.add(group.toArray((Object[]) Array.newInstance(clazz, group.size())));
            }
        }
    }

    /**
     * Prepare only one argument.
     * 
     * @param context
     *            The context.
     * @param message
     *            The message.
     * @param annotations
     *            A list of annotations.
     * @param type
     *            The object expected type..
     * @param arg
     *            The current argument.
     * @return The result of argument preparation.
     * @throws PluginException
     *             On preparation errors.
     */
    public static Object prepareArgument(IContext context, String message, Annotation[] annotations, Class<?> type, Object arg) throws PluginException {
        if (arg == null) {
            return null;
        }
        type = getWrapper(type);
        if (arg instanceof String) {
            Object tmp = UtilEvaluator.evaluate((String) arg, context, true);
            if (tmp == null || type.isInstance(tmp)) {
                return tmp;
            }
        }
        if (!type.isInstance(arg) || arg instanceof String) {
            String rawName = type.getSimpleName().toLowerCase();
            String simpleName = rawName.replace("[]", "");
            IConverter converter = null;
            Object[] converterArguments = null;
            Converter annotation = getConverter(annotations);
            IConverterManager cm = SRServices.getConverterManager();
            if (annotation != null) {
                String name = annotation.name();
                if (!name.isEmpty()) {
                    converter = cm.get(name);
                    if (converter == null) {
                        throw new PluginException("Converter named '" + name + "' not found.");
                    }
                } else {
                    Class<? extends IConverter> converterType = annotation.type();
                    if (converterType != IConverter.class) {
                        try {
                            converter = converterType.newInstance();
                        } catch (InstantiationException e) {
                            throw new PluginException(e);
                        } catch (IllegalAccessException e) {
                            throw new PluginException(e);
                        }
                    }
                    if (converter == null) {
                        // lookup for converter that match class simple name
                        converter = cm.get(simpleName);
                    }
                }
                converterArguments = annotation.args();
            } else {
                // lookup for converter that match class simple name
                converter = cm.get(simpleName);
                converterArguments = new Object[] {};
            }
            if (converter == null && SRServices.get(IObjectManager.class).isBound(type)) {
                // special converter for objects, lookup object in memory
                converter = cm.get("object");
                converterArguments = new Object[] { type };
            }
            if (converter != null) {
                try {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(converter + "(" + arg + "," + Arrays.toString(converterArguments) + ")");
                    }
                    Object tmp = converter.convert(arg, converterArguments);
                    if (!rawName.contains("[]") && !type.isInstance(tmp)) {
                        throw new PluginException("Invalid parameter value [" + arg + "] in " + message + ". Expected " + type + ", received: " + tmp + " of type " + (tmp != null ? tmp.getClass() : "null"));
                    }
                    return tmp;
                } catch (ConverterException e) {
                    throw new PluginException(e);
                }
            }
        }
        return arg;
    }

    /**
     * Extract converter information from parameter annotations.
     * 
     * @param annots
     *            The annotations.
     * @return The converter annotation if exists, null, otherwise.
     */
    public static Converter getConverter(Annotation[] annots) {
        Converter conv = null;
        for (int j = 0; j < annots.length; j++) {
            if (annots[j] instanceof Converter) {
                conv = (Converter) annots[j];
            }
        }
        return conv;
    }
}
