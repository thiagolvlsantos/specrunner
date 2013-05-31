package org.specrunner.junit;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.result.IResultSet;

/**
 * SpecRunner executor.
 * 
 * @author Thiago Santos
 * 
 */
public class SRRunner extends BlockJUnit4ClassRunner {

    /**
     * Fake method.
     */
    private static final String FAKE = "toString";

    /**
     * Allowed extensions of file.
     */
    public static final String EXTENSIONS = System.getProperty("sr.extensions", ".xhtml,.html,.htm");

    /**
     * Output path.
     */
    public static final String PATH = System.getProperty("sr.output", "target/output/");

    /**
     * Basic constructor.
     * 
     * @param clazz
     *            The test class.
     * @throws InitializationError
     *             On initialization errors.
     */
    public SRRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        try {
            return Arrays.asList(new FrameworkMethod(getTestClass().getJavaClass().getMethod(FAKE)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.describeChild(method);
        }
        return Description.createSuiteDescription(getTestClass().getJavaClass());
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, final Object test) {
        if (!method.getName().equalsIgnoreCase(FAKE)) {
            return super.methodInvoker(method, test);
        } else {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
                    cfg.add(PluginHtml.BEAN_NAME, test);
                    Class<?> clazz = getTestClass().getJavaClass();
                    File input = getFile(clazz);
                    File output = getOutput(clazz, input);
                    cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, output.getParentFile());
                    cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, output.getName());
                    IResultSet result = SpecRunnerServices.getSpecRunner().run(input.getPath(), cfg);
                    if (result.getStatus().isError()) {
                        throw new Exception(result.asString());
                    }
                }
            };
        }
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
        String[] extensions = EXTENSIONS.split(",");
        for (String s : extensions) {
            File tmp = new File(prefix + s);
            if (tmp.exists()) {
                return tmp;
            }
        }
        throw new RuntimeException("File with one of extensions '" + Arrays.toString(extensions) + "' to " + prefix + " not found!");
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
}