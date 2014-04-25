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
    private static final String FAKE = "toString";

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
            return Arrays.asList(new FrameworkMethod(getTestClass().getJavaClass().getMethod(FAKE)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.describeChild(method);
        }
        return Description.createSuiteDescription(getTestClass().getJavaClass());
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.methodInvoker(method, test);
        } else {
            return new SpecRunnerStatement(getTestClass(), test, new LinkedList<INodeListener>());
        }
    }
}