package org.specrunner.junit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
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
public class SRRunnerSpring extends SpringJUnit4ClassRunner {

    /**
     * Fake method.
     */
    protected FrameworkMethod fakeMethod;

    /**
     * Basic constructor.
     * 
     * @param clazz
     *            The test class.
     * @throws InitializationError
     *             On initialization errors.
     */
    public SRRunnerSpring(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        try {
            fakeMethod = new FrameworkMethod(getTestClass().getJavaClass().getMethod("toString"));
            return Arrays.asList(fakeMethod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method != fakeMethod) {
            return super.describeChild(method);
        }
        return Description.createSuiteDescription(getTestClass().getJavaClass());
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (method != fakeMethod) {
            return super.methodInvoker(method, test);
        } else {
            return new SpecRunnerStatement(getTestClass(), test, new LinkedList<INodeListener>());
        }
    }
}