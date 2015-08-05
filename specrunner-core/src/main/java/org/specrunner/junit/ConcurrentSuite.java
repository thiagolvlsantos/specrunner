/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
import java.util.List;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ConcurrentSuite extends Suite {

    /**
     * Creates a concurrent runner.
     * 
     * @param klass
     *            The class.
     * @throws InitializationError
     *             On initialization error.
     */
    public ConcurrentSuite(final Class<?> klass) throws InitializationError {
        super(klass, new AllDefaultPossibilitiesBuilder(true) {
            @Override
            public Runner runnerForClass(Class<?> testClass) {
                List<RunnerBuilder> builders = Arrays.asList(new RunnerBuilder() {
                    @Override
                    public Runner runnerForClass(Class<?> testClass) throws InitializationError {
                        Concurrent annotation = testClass.getAnnotation(Concurrent.class);
                        return annotation != null ? new ConcurrentRunner(testClass) : null;
                    }
                }, ignoredBuilder(), annotatedBuilder(), suiteMethodBuilder(), junit3Builder(), junit4Builder());
                for (RunnerBuilder each : builders) {
                    Runner runner = each.safeRunnerForClass(testClass);
                    if (runner != null) {
                        return runner;
                    }
                }
                return null;
            }
        });
        setScheduler(new ConcurrentRunnerScheduler(klass));
    }
}
