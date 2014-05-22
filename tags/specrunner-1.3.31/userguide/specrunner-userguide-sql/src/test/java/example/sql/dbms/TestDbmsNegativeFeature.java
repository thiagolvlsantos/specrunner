/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package example.sql.dbms;

import org.junit.Before;
import org.junit.Test;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.sql.AbstractPluginDatabase;
import org.specrunner.sql.PluginCompareBase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginFilter;
import org.specrunner.sql.PluginPrepare;
import org.specrunner.sql.PluginRelease;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.PluginScripts;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;
import org.specrunner.sql.report.FilterDefault;

//CHECKSTYLE:OFF
//@RunWith(ConcurrentRunner.class)
public class TestDbmsNegativeFeature {

    private static final String INCOME = "src/test/resources/income/dbms/";
    private static final String OUTCOME = "src/test/resources/outcome/dbms/";

    private IConfiguration cfg;

    public TestDbmsNegativeFeature() {
        cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        cfg.add(PluginSchemaLoader.FEATURE_REUSE, true);

        cfg.add(PluginSchema.FEATURE_SOURCE, "/income/dbms/schema.cfg.xml");
        cfg.add(PluginSchema.FEATURE_REUSE, true);

        cfg.add(PluginDatabase.FEATURE_PROVIDER, Database.class.getName());
        cfg.add(PluginDatabase.FEATURE_NAME, "dataA;dataB");
        cfg.add(PluginDatabase.FEATURE_REUSE, true);

        cfg.add(AbstractPluginDatabase.FEATURE_DATASOURCE, "conA|conB");
        cfg.add(AbstractPluginDatabase.FEATURE_DATABASE, "dataA|dataB");
        cfg.add(AbstractPluginDatabase.FEATURE_SEPARATOR, "|");

        cfg.add(PluginFilter.FEATURE_FILTER_INSTANCE, new FilterDefault() {
            @Override
            public boolean accept(Column column) {
                return !column.getName().equalsIgnoreCase("PRO_NM");
            }
        });

        cfg.add(PluginCompareBase.FEATURE_SYSTEM, "conA");
        cfg.add(PluginCompareBase.FEATURE_REFERENCE, "conB");
        cfg.add(PluginCompareBase.FEATURE_FILTER, PluginFilter.DEFAULT_FILTER_NAME);

        cfg.add(PluginRelease.FEATURE_NAME, "dataA;dataB");
    }

    protected void run(String name) {
        run(name, name);
    }

    protected void run(String name, String out) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + out, cfg);
    }

    @Before
    public void before() throws PluginException {
        IPluginFactory pf = SRServices.get(IPluginFactory.class);

        IPluginGroup group = new PluginGroupImpl();

        PluginConnection conA = new PluginConnection();
        conA.setConnection("org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TESTE_INICIAL|sa|");
        conA.setName("conA");
        group.add(conA);

        PluginConnection conB = new PluginConnection();
        conB.setConnection("org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TESTE_FINAL|sa|");
        conB.setName("conB");
        group.add(conB);

        PluginKind kind = PluginKind.CSS;
        pf.bind(kind, "connections", group);

        PluginScripts drop = new PluginScripts();
        drop.setValue("drop.sql");
        drop.setFailsafe(true);
        drop.setName("conA;conB");
        pf.bind(kind, "scriptsDrop", drop);

        PluginScripts schema = new PluginScripts();
        schema.setValue("schema.sql");
        schema.setName("conA;conB");
        pf.bind(kind, "scriptsSchema", schema);

        PluginScripts constraints = new PluginScripts();
        constraints.setValue("constraints.sql");
        constraints.setName("conA;conB");
        pf.bind(kind, "scriptsConstraints", constraints);

        PluginPrepare prepareB = new PluginPrepare();
        prepareB.setDatasource("conB");
        prepareB.setDatabase("dataB");
        pf.bind(kind, "prepareB", prepareB);
    }

    @Test
    public void pluginsNegativeFeature() {
        run("dbmsNegativeFeature.html");
    }
}
// CHECKSTYLE:ON
