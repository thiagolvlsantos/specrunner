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
package org.specrunner;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.comparators.IComparatorManager;
import org.specrunner.converters.IConverterManager;
import org.specrunner.core.SpecRunnerPipelineUtils;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.formatters.IFormatterManager;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.readers.IReaderManager;
import org.specrunner.util.UtilLog;

/**
 * Centralizes the services provided by the SpecRunner framework. To get full
 * list of available services list use method <code>getServices()</code>. For
 * specific services, i.e. if you need an instance of IStringAlignerFactory, use
 * <code>SRServices.get(IStringAlignerFactory.class)</code>.
 * 
 * <p>
 * <b>This is thread-safe!</b>
 * 
 * @author Thiago Santos
 * 
 */
public final class SRServices {

    /**
     * Constant of property which enables multiple SRServices (one per thread).
     */
    public static final String SR_THREAD_SAFE = "sr.threadsafe";

    /**
     * Instance global.
     */
    private static SRServices global;

    /**
     * Instance by thread.
     */
    private static ThreadLocal<SRServices> instance = new ThreadLocal<SRServices>();

    /**
     * Map of services by type.
     */
    private final Map<Class<?>, Object> servicePool = new HashMap<Class<?>, Object>();

    /**
     * Constant of property which which sets the default implementation for
     * services lookup.
     */
    public static final String SR_MAPPING = "sr.mapping";
    /**
     * Configuration.
     */
    private ISRMapping mapping;

    /**
     * Thread name.
     */
    private String threadName;

    /**
     * Create a group of services provided by SpecRunner.
     */
    private SRServices() {
        String str = System.getProperty(SR_MAPPING, "org.specrunner.core.SRMappingDefault");
        try {
            mapping = (ISRMapping) Class.forName(str).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setThreadSafe(boolean isTheadSafe) {
        System.setProperty(SR_THREAD_SAFE, String.valueOf(isTheadSafe));
    }

    /**
     * Gets the default instance of a given services.
     * 
     * @param <T>
     *            The class type.
     * @param type
     *            The type.
     * @return The service object.
     */
    private <T> T getDefault(Class<T> type) {
        Object result = null;
        if (type == SRServices.class) {
            result = this;
        } else {
            result = mapping.getDefault(type);
        }
        return type.cast(result);
    }

    /**
     * Bind a service to the SpecRunner. Be sure you create an instance of a
     * given service to each thread. This is quite simple, for example, in JUnit
     * testing set features/services using <code>@Before</code> annotation
     * instead of <code>@BeforeClass</code>. Features, however, can be set on
     * <code>@BeforeClass</code> without side-effects.
     * 
     * @param <T>
     *            The type to be bound.
     * @param clazz
     *            The service type.
     * @param instance
     *            The service instance.
     */
    public <T> void bind(Class<T> clazz, T instance) {
        servicePool.put(clazz, instance);
    }

    /**
     * Lookup for a service by its type.
     * 
     * @param <T>
     *            The service type.
     * @param clazz
     *            The service type.
     * @return The instance of the given service, or throws
     *         {@link IllegalArgumentException} if the service was not
     *         previously bound.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> clazz) {
        Object obj = servicePool.get(clazz);
        if (obj == null) {
            obj = getDefault(clazz);
            if (obj != null) {
                bind(clazz, (T) obj);
            }
        }
        if (obj == null) {
            throw new IllegalArgumentException("Object of type '" + clazz + "' not found in pool of services.");
        }
        return (T) obj;
    }

    /**
     * The map of all available services. This is the mapping itself, not a
     * copy, is up to you change as you want (take your chances).
     * 
     * @return The services mapping.
     */
    public Map<Class<?>, Object> getServices() {
        return servicePool;
    }

    /**
     * Get the thread associated to this service..
     * 
     * @return A thread name.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Shortcut static method to recover a service (thread-safe).
     * 
     * @param <T>
     *            A class type.
     * @param clazz
     *            see {@link lookup}.
     * @return see {@link lookup}.
     */
    public static <T> T get(Class<T> clazz) {
        return get().lookup(clazz);
    }

    /**
     * Gets the instance of {@link SRServices}.
     * 
     * @return The services instance.
     */
    public static SRServices get() {
        if (Boolean.valueOf(System.getProperty(SR_THREAD_SAFE, "false"))) {
            if (instance.get() == null) {
                SRServices service = new SRServices();
                addHook(service);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Thread-safe instance: " + service);
                }
                instance.set(service);
            }
            return instance.get();
        } else {
            if (global == null) {
                global = new SRServices();
                addHook(global);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Global instance: " + global);
                }
            }
            return global;
        }
    }

    /**
     * Add shutdown hook to the environment.
     * 
     * @param service
     *            A service to be shut down.
     */
    protected static void addHook(SRServices service) {
        service.threadName = Thread.currentThread().getName();
        Runtime.getRuntime().addShutdownHook(new ShutDown(service));
    }

    /**
     * Shortcut method to feature manager.
     * 
     * @return The feature manager.
     */
    public static IFeatureManager getFeatureManager() {
        return get(IFeatureManager.class);
    }

    /**
     * Shortcut method to reader manager.
     * 
     * @return The reader manager.
     */
    public static IReaderManager getReaderManager() {
        return get(IReaderManager.class);
    }

    /**
     * Shortcut method to converter manager.
     * 
     * @return The converter manager.
     */
    public static IConverterManager getConverterManager() {
        return get(IConverterManager.class);
    }

    /**
     * Shortcut method to formatter manager.
     * 
     * @return The formatter manager.
     */
    public static IFormatterManager getFormatterManager() {
        return get(IFormatterManager.class);
    }

    /**
     * Shortcut method to comparator manager.
     * 
     * @return The feature comparator.
     */
    public static IComparatorManager getComparatorManager() {
        return get(IComparatorManager.class);
    }

    /**
     * Shortcut method to feature manager.
     * 
     * @return The object manager.
     */
    public static IObjectManager getObjectManager() {
        return get(IObjectManager.class);
    }

    /**
     * Shortcut method to expression factory.
     * 
     * @return The expression factory.
     */
    public static IExpressionFactory getExpressionFactory() {
        return get(IExpressionFactory.class);
    }

    /**
     * Shortcut method to the topmost interface.
     * 
     * @return A specification runner.
     * @throws SpecRunnerException
     *             On recovering errors.
     */
    public static ISpecRunner getSpecRunner() throws SpecRunnerException {
        return get(ISpecRunnerFactory.class).newRunner();
    }

    /**
     * Shortcut method to the topmost interface for programmatic plugins.
     * 
     * @return A specification runner.
     * @throws SpecRunnerException
     *             On recovering errors.
     */
    public static ISpecRunnerPlugin getSpecRunnerPlugin() throws SpecRunnerException {
        return get(ISpecRunnerFactoryPlugin.class).newRunner();
    }

    /**
     * Release all reusable resources pending.
     */
    public static void release() {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Release programmatic call.");
        }
        release(instance.get());
    }

    /**
     * Release reusable resources.
     * 
     * @param service
     *            The service.
     */
    private static void release(SRServices service) {
        try {
            IChannel channel = service.lookup(IChannelFactory.class).newChannel();
            channel.add(ShutDown.SHUTDOWN, service);
            IPipeline pipeline = SpecRunnerPipelineUtils.getPipeline(service, "specrunner_shutdown.xml");
            pipeline.process(channel);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * Shutdown hook thread.
     * 
     * @author Thiago Santos.
     * 
     */
    public static class ShutDown extends Thread {
        /**
         * Name of service in shutdown channel.
         */
        public static final String SHUTDOWN = "shutdown";
        /**
         * The services instance to be shutdown.
         */
        private final SRServices shutdown;

        /**
         * The services instance to shutdown on <code>System.exit(...)</code>.
         * 
         * @param shutdown
         *            The instance.
         */
        public ShutDown(SRServices shutdown) {
            this.shutdown = shutdown;
        }

        @Override
        public void run() {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Release shutdown call.");
            }
            SRServices.release(shutdown);
        }

        /**
         * Return the services instance bound on channel.
         * 
         * @param channel
         *            The channel.
         * @return The service instance.
         * @throws PipelineException
         *             On lookup errors.
         */
        public static SRServices recover(IChannel channel) throws PipelineException {
            return channel.get(SHUTDOWN, SRServices.class);
        }
    }
}
