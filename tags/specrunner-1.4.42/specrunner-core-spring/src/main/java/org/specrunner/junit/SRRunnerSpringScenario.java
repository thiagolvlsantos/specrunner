package org.specrunner.junit;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.specrunner.listeners.INodeListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SpecRunner Spring executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunnerSpringScenario extends SpringJUnit4ClassRunner implements IRunnerScenario {

    /**
     * The notifier.
     */
    protected RunNotifier notifier;

    /**
     * The fake method.
     */
    protected FrameworkMethod fakeMethod;

    /**
     * Scenario listeners.
     */
    protected List<INodeListener> listeners;

    /**
     * The fixture object, if it exists, null, otherwise.
     */
    protected Object instance;

    /**
     * Statement performed.
     */
    protected SpecRunnerStatement statement;

    /**
     * Basic constructor.
     * 
     * @param clazz
     *            The test class.
     * @throws InitializationError
     *             On initialization errors.
     */
    public SRRunnerSpringScenario(Class<?> clazz) throws InitializationError {
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