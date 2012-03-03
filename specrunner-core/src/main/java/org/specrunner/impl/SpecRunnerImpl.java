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
package org.specrunner.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.ISpecRunner;
import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextFactory;
import org.specrunner.context.IContextPopulator;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultFactory;
import org.specrunner.result.IResultSet;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.transformer.ITransformer;
import org.specrunner.util.UtilEvaluator;

/**
 * Default implementation. All methods can be overridden!!!!
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerImpl implements ISpecRunner {

    public static final String TIME = "time";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public IResultSet run(String input) throws SpecRunnerException {
        return doRun(input, SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration());
    }

    @Override
    public IResultSet run(String input, IConfiguration configuration) throws SpecRunnerException {
        return doRun(input, configuration);
    }

    protected IResultSet doRun(String input, IConfiguration configuration) throws SpecRunnerException {
        // create a model information
        Map<String, Object> model = createModel();

        // feature manager
        IFeatureManager features = createFeatureManager();

        // local feature settings
        features.setCfgFeatures(configuration);

        // listeners added
        IListenerManager listeners = createListenerManager();

        // reset listeners
        listeners.reset();

        // create the source
        ISource source = createSource(input);

        // performs source transformation.
        source = transformSource(source);

        // adding default resources
        addResources(source);

        // a plugin runner
        IRunner runner = createRunner(source);

        // a new context
        IContext context = createContext(source, runner);

        // populates the context with predefined elements
        populate(context);

        // a result set
        IResultSet result = createResult();

        // annotator
        IAnnotator annotator = createAnnotator();

        // ----------- METAVARIABLES --------------
        // meta variable 'features'
        context.saveGlobal(UtilEvaluator.asVariable("features"), features);
        // meta variable 'listeners'
        context.saveGlobal(UtilEvaluator.asVariable("listeners"), listeners);
        // meta variable 'configuration'
        context.saveGlobal(UtilEvaluator.asVariable("configuration"), configuration);
        // meta variable 'source'
        context.saveGlobal(UtilEvaluator.asVariable("string"), source);
        // meta variable 'runner'
        context.saveGlobal(UtilEvaluator.asVariable("runner"), runner);
        // meta variable 'model'
        context.saveGlobal(UtilEvaluator.asVariable("model"), model);
        // meta variable 'context'
        context.saveGlobal(UtilEvaluator.asVariable("context"), context);
        // meta variable 'result'
        context.saveGlobal(UtilEvaluator.asVariable("result"), result);
        // meta variable 'annotator'
        context.saveGlobal(UtilEvaluator.asVariable("annotator"), annotator);

        // start execution
        perform(source, runner, context, result);

        // annotate result
        annotator.annotate(result);

        // add time information to model
        addTimeInfo(model);

        // message before dump
        messageBefore(input);

        // dump results
        dump(source, result, model);

        // message after dump
        messageAfter(model, result);

        return result;
    }

    protected Map<String, Object> createModel() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(TIME, System.currentTimeMillis());
        Runtime rt = Runtime.getRuntime();
        model.put("free", rt.freeMemory());
        model.put("total", rt.totalMemory());
        model.put("max", rt.maxMemory());
        return model;
    }

    protected IFeatureManager createFeatureManager() {
        return SpecRunnerServices.get(IFeatureManager.class);
    }

    protected IListenerManager createListenerManager() {
        return SpecRunnerServices.get(IListenerManager.class);
    }

    protected ISource createSource(String input) throws SourceException {
        return SpecRunnerServices.get(ISourceFactory.class).newSource(input);
    }

    protected ISource transformSource(ISource source) throws SourceException {
        return SpecRunnerServices.get(ITransformer.class).transform(source);
    }

    protected void addResources(ISource source) throws ResourceException {
        IResourceManager manager = source.getManager();
        manager.addDefaultCss();
        manager.addDefaultJs();
    }

    protected IRunner createRunner(ISource source) throws RunnerException {
        return SpecRunnerServices.get(IRunnerFactory.class).newRunner(source);
    }

    protected IContext createContext(ISource source, IRunner runner) throws ContextException {
        return SpecRunnerServices.get(IContextFactory.class).newContext(source, runner);
    }

    protected IContext populate(IContext context) throws ContextException {
        return SpecRunnerServices.get(IContextPopulator.class).populate(context);
    }

    protected IResultSet createResult() {
        return SpecRunnerServices.get(IResultFactory.class).newResult();
    }

    protected void perform(ISource source, IRunner runner, IContext context, IResultSet result) throws RunnerException {
        runner.run(source, context, result);
    }

    protected IAnnotator createAnnotator() throws AnnotatorException {
        return SpecRunnerServices.get(IAnnotatorFactory.class).newAnnotator();
    }

    protected void addTimeInfo(Map<String, Object> info) {
        info.put(TIME, System.currentTimeMillis() - (Long) info.get(TIME));
        info.put("date", sdf.format(new Date()));
    }

    protected void messageBefore(String input) {
        System.out.println();
        System.out.println(" Input " + getNome() + ": " + (input != null ? input.replace('/', File.separatorChar) : "null"));
    }

    private String getNome() {
        return "(" + Thread.currentThread().getName() + ")";
    }

    protected void dump(ISource source, IResultSet result, Map<String, Object> info) throws SourceDumperException {
        SpecRunnerServices.get(ISourceDumperFactory.class).newDumper().dump(source, result, info);
    }

    protected void messageAfter(Map<String, Object> info, IResultSet result) {
        System.out.printf("Result " + getNome() + ": %s \n", result.asString());
        System.out.printf("    In " + getNome() + ": %d mls \n", info.get(TIME));
        System.out.printf("    At " + getNome() + ": %s \n", info.get("date"));
    }

    @Override
    public IResultSet run(IPlugin plugin) throws SpecRunnerException {
        return doRun(plugin, SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration());
    }

    @Override
    public IResultSet run(IPlugin plugin, IConfiguration configuration) throws SpecRunnerException {
        return doRun(plugin, configuration);
    }

    protected IResultSet doRun(IPlugin plugin, IConfiguration configuration) throws SpecRunnerException {
        // create a model information
        Map<String, Object> model = createModel();

        // feature manager
        IFeatureManager features = createFeatureManager();

        // local feature settings
        features.setCfgFeatures(configuration);

        // listeners added
        IListenerManager listeners = createListenerManager();

        // reset listeners
        listeners.reset();

        // a plugin runner
        IRunner runner = createRunner(null);

        // a new context
        IContext context = createContext(null, runner);

        // populates the context with predefined elements
        populate(context);

        // a result set
        IResultSet result = createResult();

        // start execution
        perform(plugin, runner, context, result);

        // add time information to model
        addTimeInfo(model);

        // message before dump
        messageBefore(plugin.toString());

        // message after dump
        messageAfter(model, result);

        return result;
    }

    private void perform(IPlugin plugin, IRunner runner, IContext context, IResultSet result) throws RunnerException {
        runner.run(plugin, context, result);
    }
}