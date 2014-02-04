package org.specrunner.junit;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * SpecRunner executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunner extends BlockJUnit4ClassRunner {

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
    public SRRunner(Class<?> clazz) throws InitializationError {
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
            return new SpecRunnerStatement(getTestClass(), test);
        }
    }
}