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
package org.specrunner.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.ISRMapping;
import org.specrunner.ISpecRunnerFactory;
import org.specrunner.ISpecRunnerFactoryPlugin;
import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.comparators.IComparatorManager;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.context.IBlockFactory;
import org.specrunner.context.IContextFactory;
import org.specrunner.context.IContextPopulator;
import org.specrunner.converters.IConverterManager;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipelineFactory;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.report.IReporterFactory;
import org.specrunner.result.IResultFactory;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.source.IBuilderFactory;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.resource.IResourceManagerFactory;
import org.specrunner.transformer.ITransformerManager;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.cache.ICacheFactory;
import org.specrunner.util.output.IOutputFactory;
import org.specrunner.util.resources.ResourceFinder;
import org.specrunner.util.xom.IPresenterManager;

/**
 * The Spring mapping.
 * 
 * @author Thiago Santos
 * 
 */
public class SRMappingDefault implements ISRMapping {

    /**
     * Classes.
     */
    private Map<Class<?>, String> types = new HashMap<Class<?>, String>();
    /**
     * Configuration.
     */
    private Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    /**
     * Create a group of services provided by SpecRunner.
     */
    public SRMappingDefault() {
        types.put(ResourceFinder.class, "org.specrunner.util.resources.ResourceFinder");
        types.put(IPropertyLoader.class, "org.specrunner.properties.core.PropertyLoaderImpl");
        types.put(IConfigurationFactory.class, "org.specrunner.configuration.core.ConfigurationFactoryImpl");
        types.put(IFeatureManager.class, "org.specrunner.features.core.FeatureManagerImpl");
        types.put(IConverterManager.class, "org.specrunner.converters.core.ConverterManagerImpl");
        types.put(IComparatorManager.class, "org.specrunner.comparators.core.ComparatorManagerImpl");
        types.put(IPresenterManager.class, "org.specrunner.util.xom.core.PresenterManagerImpl");
        types.put(IAccessFactory.class, "org.specrunner.parameters.core.AccessFactoryImpl");
        types.put(IConcurrentMapping.class, "org.specrunner.concurrency.core.ConcurrentMappingImpl");
        types.put(IResourceManagerFactory.class, "org.specrunner.source.resource.core.ResourceManagerFactoryImpl");
        types.put(ISourceFactoryManager.class, "org.specrunner.source.core.SourceFactoryManagerImpl");
        types.put(IBuilderFactory.class, "org.specrunner.source.core.BuilderFactoryImpl");
        types.put(ITransformerManager.class, "org.specrunner.transformer.core.TransformerManagerImpl");
        types.put(IPluginFactory.class, "org.specrunner.plugins.core.factories.PluginFactoryGroupDefault");
        types.put(IBlockFactory.class, "org.specrunner.context.core.BlockFactoryImpl");
        types.put(IExpressionFactory.class, "org.specrunner.expressions.core.ExpressionFactoryJanino");
        types.put(IContextFactory.class, "org.specrunner.context.core.ContextFactoryImpl");
        types.put(IContextPopulator.class, "org.specrunner.context.core.ContextPopulatorImpl");
        types.put(IRunnerFactory.class, "org.specrunner.runner.core.RunnerFactoryDefault");
        types.put(IResultFactory.class, "org.specrunner.result.core.ResultFactoryImpl");
        types.put(IAnnotatorFactory.class, "org.specrunner.annotator.core.AnnotatorFactoryDefault");
        types.put(ISourceDumperFactory.class, "org.specrunner.dumper.core.SourceDumperFactoryDefault");
        types.put(IListenerManager.class, "org.specrunner.listeners.core.ListenerManagerDefault");
        types.put(IReuseManager.class, "org.specrunner.reuse.core.ReusableManagerImpl");
        types.put(IChannelFactory.class, "org.specrunner.pipeline.core.ChannelFactoryImpl");
        types.put(IPipelineFactory.class, "org.specrunner.pipeline.core.PipelineFactoryCustom");
        types.put(IStringAlignerFactory.class, "org.specrunner.util.aligner.core.StringAlignerFactoryImpl");
        types.put(ICacheFactory.class, "org.specrunner.util.cache.core.CacheFactoryDefault");
        types.put(IReporterFactory.class, "org.specrunner.report.core.ReporterFactoryDefault");
        types.put(IOutputFactory.class, "org.specrunner.util.output.core.OutputFactoryDefault");
        types.put(ISpecRunnerFactory.class, "org.specrunner.core.SpecRunnerFactoryDefault");
        types.put(ISpecRunnerFactoryPlugin.class, "org.specrunner.core.SpecRunnerFactoryPluginDefault");
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
    @SuppressWarnings("unchecked")
    public <T> T getDefault(Class<T> type) {
        long time = System.currentTimeMillis();
        Object result = map.get(type);
        if (result == null) {
            try {
                String className = types.get(type);
                Class<T> c = (Class<T>) Class.forName(className);
                result = c.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        map.put(type, result);
        if (UtilLog.LOG.isDebugEnabled()) {
            time = System.currentTimeMillis() - time;
            UtilLog.LOG.debug("Get default(" + type + ") time: " + time);
        }
        return type.cast(result);
    }
}