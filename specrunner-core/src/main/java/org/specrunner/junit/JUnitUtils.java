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
package org.specrunner.junit;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.specrunner.SRServices;
import org.specrunner.annotations.IRunnerCondition;
import org.specrunner.annotations.IScenarioListener;
import org.specrunner.annotations.SRRunnerCondition;
import org.specrunner.annotations.SRScenarioListeners;
import org.specrunner.annotations.UtilAnnotations;
import org.specrunner.context.IContext;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.core.ScenarioCleanerListener;
import org.specrunner.listeners.core.ScenarioFrameListener;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.util.UtilLog;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

import nu.xom.Node;
import nu.xom.Nodes;

/**
 * JUnit useful functions.
 * 
 * @author Thiago Santos
 * 
 */
public final class JUnitUtils {

    /**
     * Output path.
     */
    public static final String PATH = System.getProperty("sr.output", "target/output/");

    /**
     * Default constructor.
     */
    private JUnitUtils() {
    }

    /**
     * Check if a test class has to be skipped.
     * 
     * @param javaClass
     *            The fixture class.
     * @return true, to skip, false, otherwise.
     */
    public static boolean skip(Class<?> javaClass) {
        return skip(javaClass, null);
    }

    /**
     * Check if a test class has to be skipped.
     * 
     * @param javaClass
     *            The fixture class.
     * @param in
     *            The input file/resource.
     * @return true, to execute false, otherwise.
     */
    public static boolean skip(Class<?> javaClass, File in) {
        List<SRRunnerCondition> conditions = UtilAnnotations.getAnnotations(javaClass, SRRunnerCondition.class, true);
        boolean skip = false;
        File input = in;
        File output = null;
        for (SRRunnerCondition c : conditions) {
            if (input == null) {
                input = getFile(javaClass, false);
            }
            if (input != null && output == null) {
                output = getOutput(javaClass, input);
            }
            IRunnerCondition runnerCondition;
            try {
                runnerCondition = c.value().newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            skip = runnerCondition.skip(javaClass, input, output);
            if (skip) {
                break;
            }
        }
        return skip;
    }

    /**
     * Get the HTML file corresponding to the Java.
     * 
     * @param clazz
     *            The test class.
     * 
     * @return The input HTML file.
     */
    public static File getFile(Class<?> clazz, boolean throwError) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        String str;
        try {
            str = new File(location.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            throw new RuntimeException("Test class must be in a package.");
        }
        // exact match
        String path = str + File.separator + pkg.getName().replace(".", File.separator) + File.separator;
        String exact = path + clazz.getSimpleName();
        Set<String> extensions = SRServices.get(ISourceFactoryManager.class).keySet();
        for (String s : extensions) {
            File tmp = new File(exact + "." + s);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Looking for " + tmp);
            }
            if (tmp.exists()) {
                return tmp;
            }
        }
        // remove 'Test' part.
        String clean = path + clazz.getSimpleName().replace("Test", "");
        for (String s : extensions) {
            File tmp = new File(clean + "." + s);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Looking for " + tmp);
            }
            if (tmp.exists()) {
                return tmp;
            }
        }
        if (throwError) {
            throw new RuntimeException("File with one of extensions '" + extensions + "' to " + exact + " not found!");
        }
        return null;
    }

    /**
     * Get the output file.
     * 
     * @param clazz
     *            The test class.
     * @param input
     *            The input file.
     * @return The output file.
     */
    public static File getOutput(Class<?> clazz, File input) {
        return new File(new File(PATH + clazz.getPackage().getName().replace('.', File.separatorChar)).getAbsoluteFile(), input.getName());
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
        if (name != null && name.contains(".") && !name.endsWith(".html")) {
            return name.substring(0, name.lastIndexOf('.')) + ".html";
        } else {
            return name;
        }
    }

    /**
     * Given a class return instances for scenario listeners.
     * 
     * @param type
     *            The base object type.
     * @return An array of listener instances.
     */
    public static IScenarioListener[] getScenarioListener(Class<?> type) {
        List<Class<? extends IScenarioListener>> scan = scanAnnotation(type);
        if (scan.isEmpty()) {
            return new IScenarioListener[0];
        }
        IScenarioListener[] result = new IScenarioListener[scan.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = scan.get(i).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get full list of scenario listeners in annotations.
     * 
     * @param type
     *            A type.
     * @return A list of scenario listeners classes for the given type, in
     *         order, from top most class to bottom class.
     */
    public static List<Class<? extends IScenarioListener>> scanAnnotation(Class<?> type) {
        List<Class<? extends IScenarioListener>> scan = new LinkedList<Class<? extends IScenarioListener>>();
        Class<?> tmp = type;
        while (tmp != Object.class) {
            SRScenarioListeners local = tmp.getAnnotation(SRScenarioListeners.class);
            if (local != null) {
                Class<? extends IScenarioListener>[] value = local.value();
                if (value != null) {
                    for (int i = 0; i < value.length; i++) {
                        scan.add(0, value[i]);
                    }
                }
                if (!local.inheritListeners()) {
                    break;
                }
            }
            tmp = tmp.getSuperclass();
        }
        return scan;
    }

    /**
     * Prepare scenario runners.
     * 
     * @param runner
     *            Runners.
     * @return A list of methods.
     */
    public static List<FrameworkMethod> prepareScenarios(final IRunnerScenario runner) {
        List<FrameworkMethod> methods = new LinkedList<FrameworkMethod>();
        try {
            final TestClass testClass = runner.getTestClass();
            Class<?> javaClass = testClass.getJavaClass();
            Method fake = javaClass.getMethod("toString");
            FrameworkMethod fakeMethod = new FrameworkMethod(fake);
            methods.add(fakeMethod);
            runner.setFakeMethod(fakeMethod);

            List<INodeListener> listeners = new LinkedList<INodeListener>();
            runner.setListeners(listeners);

            // read scenario entries
            File input = JUnitUtils.getFile(javaClass, true);
            if (skip(javaClass, input)) {
                return methods;
            }
            ISource source = SRServices.get(ISourceFactoryManager.class).newSource(input.toString());
            Nodes scenarios = UtilNode.getCssNodesOrElements(source.getDocument(), ScenarioFrameListener.CSS_SCENARIO);
            Set<String> titles = new HashSet<String>();
            Boolean execute = null;
            for (int i = 0; i < scenarios.size(); i++) {
                Node sc = scenarios.get(i);
                INodeHolder scHolder = SRServices.get(INodeHolderFactory.class).newHolder(sc);
                if (scHolder.attributeEquals(ScenarioFrameListener.ATT_EXECUTE, "true")) {
                    if (scHolder.hasAttribute(UtilNode.IGNORE) || scHolder.hasAttribute(UtilNode.PENDING)) {
                        String title = UtilNode.getCssNodeOrElement(sc, ScenarioFrameListener.CSS_TITLE).getValue();
                        throw new RuntimeException("Scenario '" + title + "' cannot have pending='true' or ignore='true' with execute='true'. Remove pending/ignore or execute.");
                    }
                    execute = true;
                }
            }
            final Map<String, List<Description>> parentToChild = new HashMap<String, List<Description>>();
            final String txtIgnored = "IGNORED|PENDING ->";
            for (int i = 0; i < scenarios.size(); i++) {
                Node sc = scenarios.get(i);
                Node nt = UtilNode.getCssNodeOrElement(sc, ScenarioFrameListener.CSS_TITLE);
                String title = ScenarioFrameListener.getScenarioPath(nt);
                final int level = ScenarioFrameListener.getScenarioLevel(title);
                if (titles.contains(title)) {
                    throw new RuntimeException("Scenario named '" + title + "' already exists. Scenarios must have different names.");
                }
                titles.add(title);

                ScenarioFrameworkMethod scenarioMethod = new ScenarioFrameworkMethod(fake, title);
                methods.add(scenarioMethod);
                final Description description = runner.describeChild(scenarioMethod);

                IScenarioListener[] annotationListeners = JUnitUtils.getScenarioListener(javaClass);
                IScenarioListener[] fullListeners = Arrays.copyOf(annotationListeners, annotationListeners.length + 2);
                fullListeners[fullListeners.length - 1] = getScenarioCleaner(javaClass);
                final ScenarioFrameListener frameListener = new ScenarioFrameListener(title, execute, fullListeners) {
                    @Override
                    public Object getInstance() {
                        return runner.getInstance();
                    }
                };
                if (level > 1) {
                    String tmp = title.substring(0, title.lastIndexOf(ScenarioFrameListener.TITLE_TREE_SEPARATOR));
                    List<Description> children = parentToChild.get(tmp);
                    if (children == null) {
                        children = new LinkedList<Description>();
                        parentToChild.put(tmp, children);
                    }
                    children.add(description);
                }
                fullListeners[fullListeners.length - 2] = new IScenarioListener() {
                    private long time;

                    @Override
                    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
                        time = System.currentTimeMillis();
                        IResultSet r = frameListener.getResult();
                        if (frameListener.isPending() || frameListener.isIgnored()) {
                            runner.getNotifier().fireTestIgnored(description);
                            List<Description> list = parentToChild.get(title);
                            if (list != null) {
                                for (Description l : list) {
                                    runner.getNotifier().fireTestIgnored(l);
                                }
                                list.clear();
                            }
                        } else if (r == null || r.countErrors() == 0) {
                            runner.getNotifier().fireTestStarted(description);
                        }
                    }

                    @Override
                    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
                        boolean ignored = false;
                        IResultSet r = frameListener.getResult();
                        if (frameListener.isPending() || frameListener.isIgnored()) {
                            // just to not perform other things
                            ignored = true;
                        } else if (r == null || r.countErrors() == 0) {
                            runner.getNotifier().fireTestFinished(description);
                        } else {
                            StringBuilder msg = new StringBuilder("OUTPUT: ");
                            msg.append(runner.getStatement().getOutput().getAbsoluteFile());
                            msg.append('\n');
                            msg.append(r.asString());
                            runner.getNotifier().fireTestFailure(new Failure(description, new Exception(msg.toString())));
                        }
                        time = System.currentTimeMillis() - time;
                        IOutput out = SRServices.get(IOutputFactory.class).currentOutput();
                        StringBuilder tmp = new StringBuilder();
                        for (int j = 0; j < level; j++) {
                            tmp.append('\t');
                        }
                        out.printf(tmp + "Scenario (%5d ms): %-" + txtIgnored.length() + "s '%s'\n", time, (!ignored ? (r.countErrors() > 0 ? "FAIL ------------>" : "SUCCESS --------->") : txtIgnored), title);
                    }
                };
                listeners.add(frameListener);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return methods;
    }

    /**
     * Get scenario cleaner.
     * 
     * @param javaClass
     *            Test class.
     * 
     * @return A cleaner.
     */
    protected static IScenarioListener getScenarioCleaner(Class<?> javaClass) {
        return new ScenarioCleanerListener();
    }
}
