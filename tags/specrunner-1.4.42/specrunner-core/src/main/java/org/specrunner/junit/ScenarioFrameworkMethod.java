package org.specrunner.junit;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;

/**
 * Extension to hold scenario names.
 * 
 * @author Thiago Santos
 * 
 */
public class ScenarioFrameworkMethod extends FrameworkMethod {

    /**
     * A scenario name.
     */
    protected String name;

    /**
     * A scenario name.
     * 
     * @param method
     *            A method.
     * @param name
     *            A scenario name.
     */
    public ScenarioFrameworkMethod(Method method, String name) {
        super(method);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the scenario name.
     * 
     * @param name
     *            The scenario name.
     */
    public void setName(String name) {
        this.name = name;
    }
}