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
package org.specrunner.listeners.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nu.xom.Node;

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.junit.AfterScenario;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilAnnotations;

/**
 * Cleaner for scenarios considering @BeforeScenario and @AfterScenario as
 * references.
 * 
 * @author Thiago Santos.
 * 
 */
public class ScenarioCleanerListener implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
        execute(instance, BeforeScenario.class, title, node, context, result);
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
        execute(instance, AfterScenario.class, title, node, context, result);
    }

    /**
     * Execute annotated methods.
     * 
     * @param instance
     *            The fixture object, if it exists, null, otherwise.
     * @param type
     *            The annotation type to perform methods.
     * @param title
     *            The title.
     * @param node
     *            The scenario node.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @throws SpecRunnerException
     *             On calling errors.
     */
    protected void execute(Object instance, Class<? extends Annotation> type, String title, Node node, IContext context, IResultSet result) throws SpecRunnerException {
        if (instance != null) {
            for (Method m : UtilAnnotations.getAnnotatedMethods(instance, type)) {
                Class<?>[] types = m.getParameterTypes();
                try {
                    // CHECKSTYLE:OFF
                    SpecRunnerException error = new SpecRunnerException("Invalid @" + type.getSimpleName() + " method (" + m + "). Check 'ScenarioCleanerListener' source to for avaliable methods signatures.");
                    switch (types.length) {
                    case 0:
                        m.invoke(instance);
                        break;
                    case 1:
                        if (types[0].isAssignableFrom(String.class)) {
                            m.invoke(instance, title);
                        } else if (types[0].isAssignableFrom(Node.class)) {
                            m.invoke(instance, node);
                        } else if (types[0].isAssignableFrom(IContext.class)) {
                            m.invoke(instance, context);
                        } else if (types[0].isAssignableFrom(IResultSet.class)) {
                            m.invoke(instance, result);
                        } else {
                            throw error;
                        }
                        break;
                    case 2:
                        if (types[0].isAssignableFrom(String.class) && types[1].isAssignableFrom(Node.class)) {
                            m.invoke(instance, title, node);
                        } else if (types[0].isAssignableFrom(IContext.class) && types[1].isAssignableFrom(IResultSet.class)) {
                            m.invoke(instance, context, result);
                        } else {
                            throw error;
                        }
                        break;
                    case 4:
                        if (types[0].isAssignableFrom(String.class) && types[1].isAssignableFrom(Node.class) && types[2].isAssignableFrom(IContext.class) && types[3].isAssignableFrom(IResultSet.class)) {
                            m.invoke(instance, title, node, context, result);
                        } else {
                            throw error;
                        }
                        break;
                    default:
                        throw error;
                    }
                    // CHECKSTYLE:ON
                } catch (IllegalArgumentException e) {
                    throw new SpecRunnerException(e);
                } catch (IllegalAccessException e) {
                    throw new SpecRunnerException(e);
                } catch (InvocationTargetException e) {
                    throw new SpecRunnerException(e);
                }
            }
        }
    }
}