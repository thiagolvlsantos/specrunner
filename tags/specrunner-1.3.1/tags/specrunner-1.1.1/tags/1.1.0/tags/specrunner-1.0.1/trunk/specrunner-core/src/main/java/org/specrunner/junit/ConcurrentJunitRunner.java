package org.specrunner.junit;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class ConcurrentJunitRunner extends BlockJUnit4ClassRunner {
    public static final double RATE = 1.5;

    public ConcurrentJunitRunner(final Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new RunnerScheduler() {
            private ExecutorService executorService = Executors.newFixedThreadPool(klass.isAnnotationPresent(Concurrent.class) ? klass.getAnnotation(Concurrent.class).threads() : (int) (Runtime.getRuntime().availableProcessors() * RATE), new NamedThreadFactory(klass.getSimpleName()));
            private CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executorService);
            private Queue<Future<Void>> tasks = new LinkedList<Future<Void>>();

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

    static final class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;

        NamedThreadFactory(String poolName) {
            group = new ThreadGroup(poolName + "-" + poolNumber.getAndIncrement());
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
        }
    }
}
