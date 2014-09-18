/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.specrunner.ISpecRunner;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.core.SpecRunnerPipelineUtils;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.ISpecRunnerListener;
import org.specrunner.listeners.core.ScenarioFrameListener;
import org.specrunner.plugins.core.elements.PluginHtml;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilAnnotations;
import org.specrunner.util.UtilLog;

/**
 * Generic statement for SpecRunner Junit extensions.
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerStatement extends Statement {

    /**
     * The test class.
     */
    private TestClass test;
    /**
     * The test instance.
     */
    private Object instance;

    /**
     * The input file.
     */
    private File input;

    /**
     * The output file.
     */
    private File output;

    /**
     * Listener to activate.
     */
    private List<INodeListener> listeners;

    /**
     * The testing object.
     * 
     * @param test
     *            The test meta-data.
     * @param instance
     *            The test instance.
     * @param listeners
     *            The listeners to activate.
     */
    public SpecRunnerStatement(TestClass test, Object instance, List<INodeListener> listeners) {
        this.test = test;
        this.instance = instance;
        this.listeners = listeners;
        Class<?> clazz = test.getJavaClass();
        input = getInput(clazz);
        output = getOutput(clazz);
    }

    /**
     * Get input file name.
     * 
     * @param clazz
     *            The class.
     * @return The corresponding file name.
     */
    protected File getInput(Class<?> clazz) {
        return JUnitUtils.getFile(clazz);
    }

    /**
     * Get output file name.
     * 
     * @param clazz
     *            The class.
     * @return The corresponding file name.
     */
    protected File getOutput(Class<?> clazz) {
        return JUnitUtils.getOutput(clazz, input);
    }

    /**
     * Get the output file.
     * 
     * @return The output.
     */
    public File getOutput() {
        return output;
    }

    @Override
    public void evaluate() throws Throwable {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        IListenerManager lm = SRServices.get(IListenerManager.class);
        for (ISpecRunnerListener s : listeners) {
            lm.add(s);
        }
        IResultSet result = null;
        try {
            ISpecRunner srunner = SRServices.getSpecRunner();
            result = srunner.run(input.getPath(), configure(cfg));
        } finally {
            List<ScenarioFrameListener> scenarios = lm.filterByType(ScenarioFrameListener.class);
            for (ScenarioFrameListener sl : scenarios) {
                if (result != null) {
                    IResultSet tmp = sl.getResult();
                    if (tmp != null) {
                        result.removeAll(tmp);
                    }
                }
            }
            for (ISpecRunnerListener s : listeners) {
                lm.remove(s);
            }
        }
        if (result.countErrors() > 0) {
            throw new Exception("OUTPUT: " + output.getAbsoluteFile() + "\n" + result.asString());
        }
    }

    /**
     * Get expected messages if any.
     * 
     * @return The list of error messages.
     */
    protected ExpectedMessages getMessages() {
        Annotation[] ans = test.getAnnotations();
        for (Annotation an : ans) {
            if (an instanceof ExpectedMessages) {
                return (ExpectedMessages) an;
            }
        }
        return null;
    }

    /**
     * Set configuration.
     * 
     * @param cfg
     *            The configuration.
     * @return The configuration itself.
     * @throws Throwable
     *             On configuration errors.
     */
    protected IConfiguration configure(IConfiguration cfg) throws Throwable {
        Annotation[] ans = instance.getClass().getAnnotations();
        for (Annotation a : ans) {
            if (a instanceof SRRunnerOptions) {
                SRRunnerOptions options = (SRRunnerOptions) a;
                cfg.add(SpecRunnerPipelineUtils.PIPELINE_FILENAME, options.pipeline());
            }
        }
        cfg.add(PluginHtml.BEAN_NAME, instance);
        cfg.add(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY, output.getParentFile());
        cfg.add(ConstantsDumperFile.FEATURE_OUTPUT_NAME, getOutputName(output.getName()));
        ExpectedMessages expected = getMessages();
        if (expected != null) {
            cfg.add(IResultSet.FEATURE_EXPECTED_MESSAGES, expected.value());
            cfg.add(IResultSet.FEATURE_EXPECTED_SORTED, expected.sorted());
            cfg.add(IResultSet.FEATURE_EXPECTED_CRITERIA, expected.criteria().newInstance());
        }
        for (Method m : UtilAnnotations.getAnnotatedMethods(instance, Configuration.class)) {
            Class<?>[] types = m.getParameterTypes();
            if (types.length == 1 && types[0] == IConfiguration.class) {
                m.invoke(instance, new Object[] { cfg });
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Configuration method '" + m + "' invoked.");
                }
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Invalid @Configuration method '" + m + "'");
                }
            }
        }
        return cfg;
    }

    /**
     * Get the output name adjusted.
     * 
     * @param name
     *            The original name.
     * @return The adjusted name. ie. Excel (.xls,.xlsx) test files are
     *         transformed to HTML (.html).
     */
    public static String getOutputName(String name) {
        return JUnitUtils.getOutputName(name);
    }
}