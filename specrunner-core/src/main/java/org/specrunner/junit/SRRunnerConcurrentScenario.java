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
package org.specrunner.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Nodes;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.specrunner.SRServices;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.core.ScenarioListener;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.UtilNode;

/**
 * SpecRunner concurrent executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunnerConcurrentScenario extends ConcurrentRunner {

    /**
     * Fake method.
     */
    private static final String FAKE = "toString";

    /**
     * Auxiliary index.
     */
    private int index;

    /**
     * Scenario listeners.
     */
    private List<INodeListener> listeners;

    /**
     * Basic constructor.
     * 
     * @param clazz
     *            The test class.
     * @throws InitializationError
     *             On initialization errors.
     */
    public SRRunnerConcurrentScenario(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        try {
            List<FrameworkMethod> methods = new LinkedList<FrameworkMethod>();
            Method fake = getTestClass().getJavaClass().getMethod(FAKE);
            methods.add(new FrameworkMethod(fake));

            // read scenario entries
            File input = JUnitUtils.getFile(getTestClass().getJavaClass());
            ISourceFactoryManager sfm = SRServices.get(ISourceFactoryManager.class);
            ISource source = sfm.newSource(input.toString());
            Document document = source.getDocument();
            Nodes scenarios = document.query("//*[contains(@class,'" + ScenarioListener.CSS_SCENARIO + "')]");
            listeners = new LinkedList<INodeListener>();
            for (int i = 0; i < scenarios.size(); i++) {
                String title = UtilNode.getCssNode(scenarios.get(i), ScenarioListener.CSS_TITLE).getValue();
                title = UtilString.camelCase(title, true);
                methods.add(new ScenarioFrameworkMethod(fake, title));
                listeners.add(new ScenarioListener(title));
            }
            return methods;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.describeChild(method);
        }
        if (method instanceof ScenarioFrameworkMethod) {
            return Description.createTestDescription(getTestClass().getJavaClass(), ((ScenarioFrameworkMethod) method).getName());
        }
        return Description.createSuiteDescription(getTestClass().getJavaClass());
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (index++ == 0) {
            super.runChild(method, notifier);
        } else {
            Description description = Description.createTestDescription(getTestClass().getJavaClass(), ((ScenarioFrameworkMethod) method).getName());
            ScenarioListener scenario = (ScenarioListener) listeners.get(index - 2);
            IResultSet result = scenario.getResult();
            if (scenario.isPending()) {
                notifier.fireTestIgnored(description);
            } else if (result == null || !result.getStatus().isError()) {
                notifier.fireTestStarted(description);
                notifier.fireTestFinished(description);
            } else {
                notifier.fireTestFailure(new Failure(description, new Exception(result.asString())));
            }
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.methodInvoker(method, test);
        } else {
            return new SpecRunnerStatement(getTestClass(), test, listeners);
        }
    }
}