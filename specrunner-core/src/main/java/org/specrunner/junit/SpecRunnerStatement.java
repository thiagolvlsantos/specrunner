package org.specrunner.junit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.util.UtilLog;

/**
 * Generic statement for SpecRunner Junit extensions.
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerStatement extends Statement {
    /**
     * Output path.
     */
    public static final String PATH = System.getProperty("sr.output", "target/output/");

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
     * The testing object.
     * 
     * @param test
     *            The test meta-data.
     * @param instance
     *            The test instance.
     */
    public SpecRunnerStatement(TestClass test, Object instance) {
        this.test = test;
        this.instance = instance;
        Class<?> clazz = test.getJavaClass();
        input = getFile(clazz);
        output = getOutput(clazz, input);
    }

    /**
     * Get the HTML file corresponding to the Java.
     * 
     * @param clazz
     *            The test class.
     * 
     * @return The input HTML file.
     */
    protected File getFile(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        String str = location.toString();
        str = str.replace("file:\\\\", "").replace("file:///", "").replace("file:\\", "").replace("file:/", "");
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            throw new RuntimeException("Test classe must be in a package.");
        }
        String prefix = str + pkg.getName().replace(".", File.separator) + File.separator + clazz.getSimpleName();
        Set<String> extensions = SpecRunnerServices.get(ISourceFactoryManager.class).keySet();
        for (String s : extensions) {
            File tmp = new File(prefix + "." + s);
            if (tmp.exists()) {
                return tmp;
            }
        }
        throw new RuntimeException("File with one of extensions '" + extensions + "' to " + prefix + " not found!");
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
    public File getOutput(Class<?> clazz, File input) {
        return new File(new File(PATH + clazz.getPackage().getName().replace('.', File.separatorChar)).getAbsoluteFile(), input.getName());
    }

    @Override
    public void evaluate() throws Throwable {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        IResultSet result = SpecRunnerServices.getSpecRunner().run(input.getPath(), configure(cfg));
        if (result.getStatus().isError()) {
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
        cfg.add(PluginHtml.BEAN_NAME, instance);
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, output.getParentFile());
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, getOutputName(output.getName()));
        ExpectedMessages expected = getMessages();
        if (expected != null) {
            cfg.add(IResultSet.FEATURE_EXPECTED_MESSAGES, expected.messages());
            cfg.add(IResultSet.FEATURE_EXPECTED_SORTED, expected.sorted());
        }
        Method[] ms = instance.getClass().getMethods();
        List<Method> candidates = new LinkedList<Method>();
        for (Method m : ms) {
            Configuration c = m.getAnnotation(Configuration.class);
            if (c != null) {
                candidates.add(m);
            }
        }
        for (Method m : candidates) {
            Class<?>[] types = m.getParameterTypes();
            if (types.length == 1 && types[0] == IConfiguration.class) {
                m.invoke(instance, new Object[] { cfg });
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
    public String getOutputName(String name) {
        if (name != null && name.contains(".") && !name.endsWith(".html")) {
            return name.substring(0, name.lastIndexOf('.')) + ".html";
        } else {
            return name;
        }
    }
}
