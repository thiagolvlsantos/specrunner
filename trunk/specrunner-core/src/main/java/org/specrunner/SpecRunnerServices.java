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
import org.specrunner.annotator.impl.AnnotatorCss;
import org.specrunner.annotator.impl.AnnotatorFactoryImpl;
import org.specrunner.annotator.impl.AnnotatorGroupImpl;
import org.specrunner.annotator.impl.AnnotatorLink;
import org.specrunner.annotator.impl.AnnotatorStacktrace;
import org.specrunner.annotator.impl.AnnotatorTitle;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.configuration.impl.ConfigurationFactoryImpl;
import org.specrunner.context.IBlockFactory;
import org.specrunner.context.IContextFactory;
import org.specrunner.context.IContextPopulator;
import org.specrunner.context.impl.BlockFactoryImpl;
import org.specrunner.context.impl.ContextFactoryImpl;
import org.specrunner.context.impl.ContextPopulatorImpl;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.dumper.impl.SourceDumperCenter;
import org.specrunner.dumper.impl.SourceDumperFactoryImpl;
import org.specrunner.dumper.impl.SourceDumperFrame;
import org.specrunner.dumper.impl.SourceDumperGroupImpl;
import org.specrunner.dumper.impl.SourceDumperResources;
import org.specrunner.dumper.impl.SourceDumperRight;
import org.specrunner.dumper.impl.SourceDumperTop;
import org.specrunner.dumper.impl.SourceDumperWritables;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.impl.ExpressionFactoryJanino;
import org.specrunner.features.IFeatureManager;
import org.specrunner.features.impl.FeatureManagerImpl;
import org.specrunner.impl.SpecRunnerFactoryImpl;
import org.specrunner.impl.SpecRunnerImpl;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.impl.FailurePausePluginListener;
import org.specrunner.listeners.impl.ListenerManagerImpl;
import org.specrunner.listeners.impl.ProfilerPluginListener;
import org.specrunner.listeners.impl.ProfilerSourceListener;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.impl.factories.PluginFactoryCSS;
import org.specrunner.plugins.impl.factories.PluginFactoryCustom;
import org.specrunner.plugins.impl.factories.PluginFactoryElement;
import org.specrunner.plugins.impl.factories.PluginFactoryGroupImpl;
import org.specrunner.plugins.impl.factories.PluginFactoryText;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.properties.impl.PropertyLoaderImpl;
import org.specrunner.result.IResultFactory;
import org.specrunner.result.impl.ResultFactoryImpl;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.ReusableManagerImpl;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.runner.impl.RunnerFactoryImpl;
import org.specrunner.runner.impl.RunnerImpl;
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

    private static ThreadLocal<SpecRunnerServices> instance = new ThreadLocal<SpecRunnerServices>();

    /**
     * Map of services by type.
     */
    private final Map<Class<?>, Object> servicePool = new HashMap<Class<?>, Object>();

    private SpecRunnerServices() {
        bind(IPropertyLoader.class, new PropertyLoaderImpl());

        bind(IConfigurationFactory.class, new ConfigurationFactoryImpl());

        bind(IFeatureManager.class, new FeatureManagerImpl());

        bind(IResourceManagerFactory.class, new ResourceManagerFactoryImpl());

        bind(IConverterManager.class, new ConverterManagerImpl());

        bind(IComparatorManager.class, new ComparatorManagerImpl());

        bind(ISourceFactory.class, new SourceFactoryImpl());

        bind(ITransformer.class, new TransformerImpl());

        bind(IPluginFactory.class, new PluginFactoryGroupImpl().add(new PluginFactoryCSS()).add(new PluginFactoryElement()).add(new PluginFactoryCustom()).add(new PluginFactoryText()));

        bind(IBlockFactory.class, new BlockFactoryImpl());

        bind(IExpressionFactory.class, new ExpressionFactoryJanino());

        bind(IContextFactory.class, new ContextFactoryImpl());

        bind(IContextPopulator.class, new ContextPopulatorImpl());

        bind(IRunnerFactory.class, new RunnerFactoryImpl(new RunnerImpl()));

        bind(IResultFactory.class, new ResultFactoryImpl());

        bind(IAnnotatorFactory.class, new AnnotatorFactoryImpl(new AnnotatorGroupImpl().add(new AnnotatorCss()).add(new AnnotatorTitle()).add(new AnnotatorStacktrace()).add(new AnnotatorLink())));

        bind(ISourceDumperFactory.class, new SourceDumperFactoryImpl(new SourceDumperGroupImpl().add(new SourceDumperResources()).add(new SourceDumperWritables()).add(new SourceDumperTop()).add(new SourceDumperCenter()).add(new SourceDumperRight()).add(new SourceDumperFrame())));

        bind(IStringAlignerFactory.class, new StringAlignerFactoryImpl());

        IListenerManager lm = new ListenerManagerImpl();
        lm.add(new ProfilerSourceListener());
        lm.add(new ProfilerPluginListener());
        lm.add(new FailurePausePluginListener());
        bind(IListenerManager.class, lm);

        bind(IReusableManager.class, new ReusableManagerImpl());

        bind(ISpecRunnerFactory.class, new SpecRunnerFactoryImpl(new SpecRunnerImpl()));
    }

    /**
     * Bind a service to the SpecRunner. Be sure you create an instance of a
     * given service to each thread. This is quite simple, for example, in JUnit
     * testing set features/services using <code>@Before</code> annotation
     * instead of <code>@BeforeClass</code>. Features, however, can be set on
     * <code>@BeforeClass</code> without side-effects.
     * 
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
     * @param clazz
     *            see {@link lookup}.
     * @return see {@link lookup}.
     */
    public static <T> T get(Class<T> clazz) {
        return get().lookup(clazz);
    }

    /**
     * Gets the instanceof of {@link SpecRunnerServices}.
     * 
     * @return
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
     * Release all reusable resources pending.
     */
    public static void release() {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Release programmatic call.");
        }
        release(instance.get());
    }

    private static void release(SpecRunnerServices service) {
        IReusableManager rm = service.lookup(IReusableManager.class);
        for (IReusable r : rm.values()) {
            r.release();
            rm.remove(r);
        }
    }

    private static class ShutDown extends Thread {
        private final SpecRunnerServices shutdown;

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
    }
}