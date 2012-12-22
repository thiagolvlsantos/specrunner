/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
import java.util.Queue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class ConcurrentSuite extends Suite {
    /**
     * Thread creation ration.
     */
    public static final double RATE = 1.5;

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
            public Runner runnerForClass(Class<?> testClass) throws Throwable {
                List<RunnerBuilder> builders = Arrays.asList(new RunnerBuilder() {
                    @Override
                    public Runner runnerForClass(Class<?> testClass) throws Throwable {
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
        setScheduler(new RunnerScheduler() {
            private final ExecutorService executorService = Executors.newFixedThreadPool(klass.isAnnotationPresent(Concurrent.class) ? klass.getAnnotation(Concurrent.class).threads() : (int) (Runtime.getRuntime().availableProcessors() * RATE), new NamedFactory(klass.getSimpleName()));
            private final CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executorService);
            private final Queue<Future<Void>> tasks = new LinkedList<Future<Void>>();

            @Override
            public void schedule(Runnable childStatement) {
                tasks.offer(completionService.submit(childStatement, null));
            }

            @Override
            public void finished() {
                try {
                    while (!tasks.isEmpty()) {
                        tasks.remove(completionService.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    while (!tasks.isEmpty()) {
                        tasks.poll().cancel(true);
                    }
                    executorService.shutdownNow();
                }
            }
        });
    }
}
