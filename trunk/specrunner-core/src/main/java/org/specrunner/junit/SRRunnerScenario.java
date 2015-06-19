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

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.specrunner.listeners.INodeListener;

/**
 * SpecRunner executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunnerScenario extends BlockJUnit4ClassRunner implements IRunnerScenario {

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
    public RunNotifier getNotifier() {
        return notifier;
    }

    @Override
    public FrameworkMethod getFakeMethod() {
        return fakeMethod;
    }

    @Override
    public void setFakeMethod(FrameworkMethod fakeMethod) {
        this.fakeMethod = fakeMethod;
    }

    @Override
    public List<INodeListener> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(List<INodeListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public SpecRunnerStatement getStatement() {
        return statement;
    }

    @Override
    public void run(RunNotifier notifier) {
        this.notifier = notifier;
        super.run(notifier);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return JUnitUtils.prepareScenarios(this);
    }

    @Override
    public Description describeChild(FrameworkMethod method) {
        if (method != fakeMethod) {
            return Description.createTestDescription(getTestClass().getJavaClass(), testName(method), method.getAnnotations());
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
            free();
        }
    }

    /**
     * Clean resource references to avoid JUnit overload.
     */
    protected void free() {
        notifier = null;
        fakeMethod = null;
        listeners = null;
        instance = null;
        statement = null;
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