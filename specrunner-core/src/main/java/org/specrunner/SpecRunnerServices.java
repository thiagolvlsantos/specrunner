/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.IPipelineFactory;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.util.UtilLog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Centralizes the services provided by the SpecRunner framework. To get full
 * list of available services list use method <code>getServices()</code>. For
 * specific services, i.e. if you need an instance of IStringAlignerFactory, use
 * <code>SpecRunnerServices.get(IStringAlignerFactory.class)</code>.
 * 
 * <p>
 * <b>This is thread-safe!</b>
 * 
 * @author Thiago Santos
 * 
 */
public final class SpecRunnerServices {

    /**
     * Instance by thread.
     */
    private static ThreadLocal<SpecRunnerServices> instance = new ThreadLocal<SpecRunnerServices>();

    /**
     * Map of services by type.
     */
    private final Map<Class<?>, Object> servicePool = new HashMap<Class<?>, Object>();

    /**
     * Configuration.
     */
    private ApplicationContext context;

    /**
     * Create a group of services provided by SpecRunner.
     */
    private SpecRunnerServices() {
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
        if (type == SpecRunnerServices.class) {
            result = this;
        } else {
            if (context == null) {
                context = new ClassPathXmlApplicationContext("applicationContext-SR.xml");
            }
            result = context.getBean(type);
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
     * Gets the instance of {@link SpecRunnerServices}.
     * 
     * @return The services instance.
     */
    public static SpecRunnerServices get() {
        if (instance.get() == null) {
            SpecRunnerServices service = new SpecRunnerServices();
            Runtime.getRuntime().addShutdownHook(new ShutDown(service));
            instance.set(service);
        }
        return instance.get();
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
    private static void release(SpecRunnerServices service) {
        try {
            IChannel channel = service.lookup(IChannelFactory.class).newChannel();
            channel.add(ShutDown.SHUTDOWN, service);
            IPipeline pipeline = service.lookup(IPipelineFactory.class).newPipeline("specrunner_shutdown.xml");
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
        private final SpecRunnerServices shutdown;

        /**
         * The services instance to shutdown on <code>System.exit(...)</code>.
         * 
         * @param shutdown
         *            The instance.
         */
        public ShutDown(SpecRunnerServices shutdown) {
            this.shutdown = shutdown;
        }

        @Override
        public void run() {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Release shutdown call.");
            }
            SpecRunnerServices.release(shutdown);
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
        public static SpecRunnerServices recover(IChannel channel) throws PipelineException {
            return channel.get(SHUTDOWN, SpecRunnerServices.class);
        }
    }
}