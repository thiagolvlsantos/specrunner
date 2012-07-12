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
package org.specrunner;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.annotator.impl.AnnotatorFactoryDefault;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.concurrency.impl.ConcurrentMappingImpl;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.configuration.impl.ConfigurationFactoryImpl;
import org.specrunner.context.IBlockFactory;
import org.specrunner.context.IContextFactory;
import org.specrunner.context.IContextPopulator;
import org.specrunner.context.impl.BlockFactoryImpl;
import org.specrunner.context.impl.ContextFactoryImpl;
import org.specrunner.context.impl.ContextPopulatorImpl;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.dumper.impl.SourceDumperFactoryDefault;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.impl.ExpressionFactoryJanino;
import org.specrunner.features.IFeatureManager;
import org.specrunner.features.impl.FeatureManagerImpl;
import org.specrunner.impl.SpecRunnerFactoryDefault;
import org.specrunner.impl.SpecRunnerFactoryPluginDefault;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.impl.ListenerManagerDefault;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.IPipelineFactory;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.pipeline.impl.ChannelFactoryImpl;
import org.specrunner.pipeline.impl.PipelineFactoryXOM;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.impl.factories.PluginFactoryGroupDefault;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.properties.impl.PropertyLoaderImpl;
import org.specrunner.report.IReporterFactory;
import org.specrunner.report.impl.ReporterFactoryDefault;
import org.specrunner.result.IResultFactory;
import org.specrunner.result.impl.ResultFactoryImpl;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.ReusableManagerImpl;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.runner.impl.RunnerFactoryDefault;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.impl.SourceFactoryImpl;
import org.specrunner.source.resource.IResourceManagerFactory;
import org.specrunner.source.resource.impl.ResourceManagerFactoryImpl;
import org.specrunner.transformer.ITransformer;
import org.specrunner.transformer.impl.TransformerImpl;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.StringAlignerFactoryImpl;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.comparer.impl.ComparatorManagerImpl;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.converter.impl.ConverterManagerImpl;

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
        if (type == IPropertyLoader.class) {
            result = new PropertyLoaderImpl();
        } else if (type == IConfigurationFactory.class) {
            result = new ConfigurationFactoryImpl();
        } else if (type == IFeatureManager.class) {
            result = new FeatureManagerImpl();
        } else if (type == IConcurrentMapping.class) {
            result = new ConcurrentMappingImpl();
        } else if (type == IResourceManagerFactory.class) {
            result = new ResourceManagerFactoryImpl();
        } else if (type == IConverterManager.class) {
            result = new ConverterManagerImpl();
        } else if (type == IComparatorManager.class) {
            result = new ComparatorManagerImpl();
        } else if (type == ISourceFactory.class) {
            result = new SourceFactoryImpl();
        } else if (type == ITransformer.class) {
            result = new TransformerImpl();
        } else if (type == IPluginFactory.class) {
            result = new PluginFactoryGroupDefault();
        } else if (type == IBlockFactory.class) {
            result = new BlockFactoryImpl();
        } else if (type == IExpressionFactory.class) {
            result = new ExpressionFactoryJanino();
        } else if (type == IContextFactory.class) {
            result = new ContextFactoryImpl();
        } else if (type == IContextPopulator.class) {
            result = new ContextPopulatorImpl();
        } else if (type == IRunnerFactory.class) {
            result = new RunnerFactoryDefault();
        } else if (type == IResultFactory.class) {
            result = new ResultFactoryImpl();
        } else if (type == IAnnotatorFactory.class) {
            result = new AnnotatorFactoryDefault();
        } else if (type == ISourceDumperFactory.class) {
            result = new SourceDumperFactoryDefault();
        } else if (type == IStringAlignerFactory.class) {
            result = new StringAlignerFactoryImpl();
        } else if (type == IListenerManager.class) {
            result = new ListenerManagerDefault();
        } else if (type == IReusableManager.class) {
            result = new ReusableManagerImpl();
        } else if (type == IChannelFactory.class) {
            result = new ChannelFactoryImpl();
        } else if (type == IReporterFactory.class) {
            result = new ReporterFactoryDefault();
        } else if (type == IPipelineFactory.class) {
            result = new PipelineFactoryXOM();
        } else if (type == ISpecRunnerFactory.class) {
            result = new SpecRunnerFactoryDefault();
        } else if (type == ISpecRunnerFactoryPlugin.class) {
            result = new SpecRunnerFactoryPluginDefault();
        } else if (type == SpecRunnerServices.class) {
            result = this;
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