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

import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.sql.AbstractPluginDatabase;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.impl.Database;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

//CHECKSTYLE:OFF
//@RunWith(ConcurrentRunner.class)
public class TestDbmsNegativeFeature extends TestDbms {

    @Before
    public void before() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        fm.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
        fm.add(PluginSchemaLoader.FEATURE_REUSE, true);
        fm.add(PluginSchema.FEATURE_SOURCE, "/income/dbms/schema.cfg.xml");
        fm.add(PluginSchema.FEATURE_REUSE, true);
        fm.add(PluginDatabase.FEATURE_PROVIDER, Database.class.getName());
        fm.add(PluginDatabase.FEATURE_REUSE, true);
        fm.add(AbstractPluginDatabase.FEATURE_DATASOURCE, "conA|conB");
        fm.add(AbstractPluginDatabase.FEATURE_DATABASE, "dataA|dataB");
        fm.add(AbstractPluginDatabase.FEATURE_SEPARATOR, "|");
    }

    @Test
    public void pluginsNegativeFeature() {
        run("dbmsNegativeFeature.html");
    }
}
// CHECKSTYLE:ON
