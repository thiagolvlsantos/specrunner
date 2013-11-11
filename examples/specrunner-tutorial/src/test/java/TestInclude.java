import org.junit.Test;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;

public class TestInclude {

    @Test
    public void testLocal() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginInclude.FEATURE_EXPANDED, Boolean.TRUE);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/include.html", cfg);
    }

    @Test
    public void testLocal2() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginInclude.FEATURE_EXPANDED, Boolean.TRUE);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/include.html", "src/test/resources/outcome/include2.html", cfg);
    }

    public static void main(String[] args) {
        try {
            ISource s;
            s = SRServices.get(ISourceFactory.class).newSource("src/test/resources/income/include.html");
            System.out.println(s.getDocument().toXML());
            s = SRServices.get(ISourceFactory.class).newSource("src/test/resources/income/dir/destino.html");
            System.out.println(s.getDocument().toXML());
            s = SRServices.get(ISourceFactory.class).newSource("src/test/resources/income/dir/sub/interno.html");
            System.out.println(s.getDocument().toXML());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}