/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
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
public class SRRunner extends SRBlockJUnit4ClassRunner {

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
