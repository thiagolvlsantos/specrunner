/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package example.sql.basic;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginScripts;

import example.sql.DataSourceProviderImpl;

// CHECKSTYLE:OFF
@RunWith(ConcurrentRunner.class)
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
        IExpressionFactory ief = SRServices.getExpressionFactory();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        ief.bindValue("pattern", pattern);
        ief.bindValue("time", "{ts '" + new DateTime().toString(pattern) + "'}");
        ief.bindClass("dt", DateTime.class);

        IFeatureManager fm = SRServices.getFeatureManager();
        // setting plugin connection features
        fm.add(PluginConnection.FEATURE_DRIVER, "org.hsqldb.jdbcDriver");
        fm.add(PluginConnection.FEATURE_URL, "jdbc:hsqldb:mem:TESTE");
        fm.add(PluginConnection.FEATURE_USER, "sa");
        fm.add(PluginConnection.FEATURE_PASSWORD, "");
        // the above code could be replaced by
        // fm.add(PluginConnection.FEATURE_CONNECTION, "org.hsqldb.jdbcDriver" +
        // PluginConnection.DEFAULT_SEPARATOR + "jdbc:hsqldb:mem:TESTE" +
        // PluginConnection.DEFAULT_SEPARATOR + "sa" +
        // PluginConnection.DEFAULT_SEPARATOR + "");

        fm.add(PluginConnection.FEATURE_REUSE, Boolean.TRUE);
        // setting plugin script features
        fm.add(PluginScripts.FEATURE_FAILSAFE, Boolean.TRUE);
    }

    @Test
    public void create() {
        run("create.html", "create.html");
    }

    @Test
    public void createProvider1() { // associa provider por sua classe
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginConnection.FEATURE_PROVIDER, DataSourceProviderImpl.class.getName());
        run("createProvider.html", "createProviderClass.html", cfg);
    }

    @Test
    public void createProvider2() { // associa provider pela instancia
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
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
}
// CHECKSTYLE:ON
