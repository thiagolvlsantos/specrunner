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
package org.specrunner.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.xom.Node;
import nu.xom.Nodes;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.listeners.core.ScenarioCleanerListener;
import org.specrunner.listeners.core.ScenarioFrameListener;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.util.string.UtilString;
import org.specrunner.util.xom.UtilNode;

/**
 * SpecRunner executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunnerScenario extends BlockJUnit4ClassRunner {

    /**
     * The notifier.
     */
    private RunNotifier notifier;

    /**
     * The fake method.
     */
    private FrameworkMethod fakeMethod;

    /**
     * Scenario listeners.
     */
    private List<INodeListener> listeners;

    /**
     * The fixture object, if it exists, null, otherwise.
     */
    private Object instance;

    /**
     * Statement performed.
     */
    private SpecRunnerStatement statement;

    /**
     * Basic constructor.
     * 
     * @param clazz
     *            The test class.
     * @throws InitializationError
     *             On initialization errors.
     */
    public SRRunnerScenario(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        this.notifier = notifier;
        super.run(notifier);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> methods = new LinkedList<FrameworkMethod>();
        try {
            final TestClass testClass = getTestClass();
            Class<?> javaClass = testClass.getJavaClass();
            Method fake = javaClass.getMethod("toString");
            fakeMethod = new FrameworkMethod(fake);
            methods.add(fakeMethod);

            // read scenario entries
            File input = JUnitUtils.getFile(javaClass);
            ISource source = SRServices.get(ISourceFactoryManager.class).newSource(input.toString());
            Nodes scenarios = UtilNode.getCssNodesOrElements(source.getDocument(), ScenarioFrameListener.CSS_SCENARIO);
            listeners = new LinkedList<INodeListener>();
            Set<String> titles = new HashSet<String>();
            for (int i = 0; i < scenarios.size(); i++) {
                Node sc = scenarios.get(i);
                String title = UtilNode.getCssNodeOrElement(sc, ScenarioFrameListener.CSS_TITLE).getValue();
                title = UtilString.getNormalizer().camelCase(title, true);
                if (titles.contains(title)) {
                    throw new RuntimeException("Scenario named '" + title + "' already exists. Scenarios must have different names.");
                }
                titles.add(title);

                ScenarioFrameworkMethod scenarioMethod = new ScenarioFrameworkMethod(fake, title);
                methods.add(scenarioMethod);
                final Description description = describeChild(scenarioMethod);

                IScenarioListener[] annotationListeners = JUnitUtils.getScenarioListener(javaClass);
                IScenarioListener[] fullListeners = Arrays.copyOf(annotationListeners, annotationListeners.length + 2);
                fullListeners[fullListeners.length - 1] = new ScenarioCleanerListener();
                final ScenarioFrameListener frameListener = new ScenarioFrameListener(title, fullListeners) {
                    @Override
                    public Object getInstance() {
                        return instance;
                    }
                };
                fullListeners[fullListeners.length - 2] = new IScenarioListener() {
                    @Override
                    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
                        IResultSet r = frameListener.getResult();
                        if (frameListener.isPending() || frameListener.isIgnored()) {
                            notifier.fireTestIgnored(description);
                        } else if (r == null || r.countErrors() == 0) {
                            notifier.fireTestStarted(description);
                        }
                    }

                    @Override
                    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
                        IResultSet r = frameListener.getResult();
                        if (frameListener.isPending() || frameListener.isIgnored()) {
                            notifier.fireTestIgnored(description);
                        } else if (r == null || r.countErrors() == 0) {
                            notifier.fireTestFinished(description);
                        } else {
                            String msg = "OUTPUT: " + statement.getOutput().getAbsoluteFile() + "\n" + r.asString();
                            notifier.fireTestFailure(new Failure(description, new Exception(msg)));
                        }
                    }
                };
                listeners.add(frameListener);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return methods;
    }

    @Override
    public Description describeChild(FrameworkMethod method) {
        if (method != fakeMethod) {
            return super.describeChild(method);
        }
        if (method instanceof ScenarioFrameworkMethod) {
            return Description.createTestDescription(getTestClass().getJavaClass(), ((ScenarioFrameworkMethod) method).getName());
        }
        return Description.createSuiteDescription(getTestClass().getJavaClass());
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (method == fakeMethod) {
            super.runChild(method, notifier);
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (method != fakeMethod) {
            return super.methodInvoker(method, test);
        } else {
            instance = test;
            statement = new SpecRunnerStatement(getTestClass(), test, listeners);
            return statement;
        }
    }
}