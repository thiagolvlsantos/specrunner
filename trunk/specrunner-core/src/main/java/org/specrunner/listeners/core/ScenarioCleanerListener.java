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
import java.util.List;

import nu.xom.Node;

import org.junit.After;
import org.junit.Before;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;

/**
 * Cleaner for scenarios considering @Before and @After as references.
 * 
 * @author Thiago Santos.
 * 
 */
public class ScenarioCleanerListener implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, TestClass test, Object instance) throws SpecRunnerException {
        execute(test, instance, Before.class);
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, TestClass test, Object instance) throws SpecRunnerException {
        execute(test, instance, After.class);
    }

    /**
     * 
     * @param test
     *            The test class wrapper.
     * @param instance
     *            The fixture object, if it exists, null, otherwise.
     * @param type
     *            The annotation type to perform methods.
     * @throws SpecRunnerException
     *             On calling errors.
     */
    protected void execute(TestClass test, Object instance, Class<? extends Annotation> type) throws SpecRunnerException {
        if (instance != null) {
            List<FrameworkMethod> methods = test.getAnnotatedMethods(type);
            for (FrameworkMethod fm : methods) {
                try {
                    fm.getMethod().invoke(instance);
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