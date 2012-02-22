package example.sql;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginScript;

public class TestSqlFeature {

    private static final String INCOME = "src/test/resources/income/clean/";
    private static final String OUTCOME = "src/test/resources/outcome/clean/";

    private void run(String in, String out) {
        SpecRunnerJUnit.defaultRun(INCOME + in, OUTCOME + out);
    }

    private void run(String in, String out, IConfiguration cfg) {
        SpecRunnerJUnit.defaultRun(INCOME + in, OUTCOME + out, cfg);
    }

    @Before
    public void before() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        // setting plugin connection features
        fm.add(PluginConnection.FEATURE_DRIVER, "org.hsqldb.jdbcDriver");
        fm.add(PluginConnection.FEATURE_URL, "jdbc:hsqldb:mem:TESTE");
        fm.add(PluginConnection.FEATURE_USER, "sa");
        fm.add(PluginConnection.FEATURE_PASSWORD, "");
        fm.add(PluginConnection.FEATURE_REUSE, Boolean.TRUE);
        // setting plugin script features
        fm.add(PluginScript.FEATURE_FAILSAFE, Boolean.TRUE);
    }

    @Test
    public void create() {
        run("create.html", "create.html");
    }

    @Test
    public void createProvider1() { // associa provider por sua classe
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginConnection.FEATURE_PROVIDER, DataSourceProviderImpl.class.getName());
        run("createProvider.html", "createProviderClass.html", cfg);
    }

    @Test
    public void createProvider2() { // associa provider pela instancia
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        run("createProvider.html", "createProviderInstance.html", cfg);
    }

    @Test
    public void drop() {
        run("drop.html", "drop.html");
    }

    @Test
    public void all() {
        run("all.html", "all.html");
    }

    @AfterClass
    public static void afterClass() {
        SpecRunnerServices.release();
    }
}