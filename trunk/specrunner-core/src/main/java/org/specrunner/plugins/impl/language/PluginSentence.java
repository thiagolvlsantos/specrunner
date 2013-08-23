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

import java.lang.reflect.Method;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;

/**
 * The method lookup strategy is performed <b>AFTER CONVERSION</b> of arguments.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginSentence extends AbstractPluginLanguage {

    @Override
    protected Method getMethod(Object target, String method, List<Object> args) throws PluginException {
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

    @Override
    protected void prepareArguments(IContext context, Method m, List<Object> args) throws PluginException {
        // nothing to change.
    }
}