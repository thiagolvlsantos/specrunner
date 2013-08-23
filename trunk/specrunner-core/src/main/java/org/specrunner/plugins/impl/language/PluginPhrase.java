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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;

/**
 * The method lookup strategy is performed <b>BEFORE CONVERSION</b> conversion
 * of arguments.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginPhrase extends AbstractPluginLanguage {

    @Override
    protected Method getMethod(Object target, String method, List<Object> args) throws PluginException {
        Class<?> type = target.getClass();
        Method[] methods = type.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method) && m.getParameterTypes().length == args.size()) {
                return m;
            }
        }
        throw new PluginException("Method named '" + method + "' with " + args.size() + " parameter(s) not found for " + type);
    }

    @Override
    protected void prepareArguments(IContext context, Method method, List<Object> arguments) throws PluginException {
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
                    } else {
                        try {
                            converter = annotation.type().newInstance();
                        } catch (InstantiationException e) {
                            throw new PluginException(e);
                        } catch (IllegalAccessException e) {
                            throw new PluginException(e);
                        }
                    }
                    converterArguments = annotation.args();
                } else {
                    converter = cm.get(type.getSimpleName().toLowerCase());
                    converterArguments = new Object[] {};
                }
                if (converter != null) {
                    try {
                        Object tmp = converter.convert(arg, converterArguments);
                        if (!type.isInstance(tmp)) {
                            throw new PluginException("Invalid parameter value for argument(" + i + ") in " + method + ". Expected " + type + ", received: " + tmp + " of type " + tmp.getClass());
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

}