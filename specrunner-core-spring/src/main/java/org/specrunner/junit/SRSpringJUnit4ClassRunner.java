/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SpecRunner Spring executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

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
    public SRSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        if (skip()) {
            for (FrameworkMethod fm : computeTestMethods()) {
                notifier.fireTestIgnored(describeChild(fm));
            }
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    // nothing
                }
            };
        }
        return super.classBlock(notifier);
    }

    /**
     * Check if method has to be skipped.
     * 
     * @return true, to execute, false, otherwise.
     * @throws Exception
     *             On verification errors.
     */
    protected boolean skip() {
        return JUnitUtils.skip(getTestClass().getJavaClass());
    }
}
