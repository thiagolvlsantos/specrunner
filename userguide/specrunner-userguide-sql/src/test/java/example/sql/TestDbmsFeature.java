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
package example.sql;

import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

//CHECKSTYLE:OFF
//@RunWith(ConcurrentRunner.class)
public class TestDbmsFeature {

    private static final String INCOME = "src/test/resources/income/dbms/";
    private static final String OUTCOME = "src/test/resources/outcome/dbms/";

    static {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        fm.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderImpl());
        fm.add(PluginConnection.FEATURE_REUSE, true);
        fm.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        fm.add(PluginSchemaLoader.FEATURE_REUSE, true);
        fm.add(PluginSchema.FEATURE_SOURCE, "/income/dbms/schema.cfg.xml");
        fm.add(PluginSchema.FEATURE_REUSE, true);
        fm.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new Database());
        fm.add(PluginDatabase.FEATURE_REUSE, true);
    }

    private void run(String name, String out) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + out);
    }

    @Test
    public void pluginsSQL() {
        run("dbmsFeature.html", "dbmsFeature.html");
    }

    @Test
    public void pluginsSQL2() {
        run("dbmsFeature.html", "dbmsFeature2.html");
    }
}
// CHECKSTYLE:ON
