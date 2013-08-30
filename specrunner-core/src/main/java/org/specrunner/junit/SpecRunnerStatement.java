package org.specrunner.junit;

import java.io.File;
import java.lang.annotation.Annotation;
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
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISourceFactoryManager;

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
    }

    @Override
    public void evaluate() throws Throwable {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginHtml.BEAN_NAME, instance);
        Class<?> clazz = test.getJavaClass();
        File input = getFile(clazz);
        File output = getOutput(clazz, input);
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, output.getParentFile());
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, getOutputName(output.getName()));
        IResultSet result = SpecRunnerServices.getSpecRunner().run(input.getPath(), cfg);
        ExpectedMessages expectedMessages = getMessages();
        if (expectedMessages == null) {
            if (result.getStatus().isError()) {
                throw new Exception("OUTPUT: " + output.getAbsoluteFile() + "\n" + result.asString());
            }
        } else {
            List<String> received = new LinkedList<String>();
            for (IResult r : result) {
                if (r.getStatus().isError()) {
                    received.add(r.getMessage() != null ? r.getMessage() : r.getFailure().getMessage());
                }
            }
            String[] messages = expectedMessages.messages();
            boolean sorted = expectedMessages.sorted();
            StringBuilder errors = new StringBuilder();
            List<String> expected = new LinkedList<String>();
            for (String s : messages) {
                expected.add(s);
            }
            if (sorted) {
                int i = 0;
                int max = Math.min(received.size(), expected.size());
                for (; i < max; i++) {
                    if (!expected.get(i).equals(received.get(i))) {
                        errors.append("Expected '" + expected.get(i) + "' does not match received '" + received.get(i) + "'.\n");
                    }
                }
                if (max < expected.size()) {
                    errors.append("Expected messages missing:" + expected.subList(max, expected.size()));
                }
                if (max < received.size()) {
                    errors.append("Unexpected messages received:" + received.subList(max, received.size()));
                }
            } else {
                List<String> extraReceived = new LinkedList<String>(received);
                List<String> extraExpected = new LinkedList<String>(expected);
                extraReceived.removeAll(expected);
                extraExpected.removeAll(received);
                if (extraExpected.size() > 0) {
                    errors.append("Expected messages missing:" + extraExpected + ".\n");
                }
                if (extraReceived.size() > 0) {
                    errors.append("Unexpected messages received:" + extraReceived + ".\n");
                }
            }
            if (errors.length() != 0) {
                throw new Exception(errors.toString());
            }
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
        String prefix = str + clazz.getPackage().getName().replace(".", File.separator) + File.separator + clazz.getSimpleName();
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
