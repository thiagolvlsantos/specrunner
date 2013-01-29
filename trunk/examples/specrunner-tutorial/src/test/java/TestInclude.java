import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.impl.include.PluginInclude;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;

public class TestInclude {

    @Test
    public void testLocal() throws Exception {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginInclude.FEATURE_EXPANDED, Boolean.TRUE);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/include.html", cfg);
    }

    public static void main(String[] args) {
        try {
            ISource s;
            s = SpecRunnerServices.get(ISourceFactory.class).newSource("src/test/resources/income/include.html");
            System.out.println(s.getDocument().toXML());
            s = SpecRunnerServices.get(ISourceFactory.class).newSource("src/test/resources/income/dir/destino.html");
            System.out.println(s.getDocument().toXML());
            s = SpecRunnerServices.get(ISourceFactory.class).newSource("src/test/resources/income/dir/sub/interno.html");
            System.out.println(s.getDocument().toXML());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}