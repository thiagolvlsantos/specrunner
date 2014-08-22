/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.sql.negative;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.type.Command;
import org.specrunner.sql.AbstractPluginDatabase;
import org.specrunner.sql.PluginCompareBase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginFilter;
import org.specrunner.sql.PluginRelease;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.PluginScripts;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

/**
 * Database macro.
 * 
 * @author Thiago Santos
 */
public class PluginDbms extends PluginGroupImpl {

    /**
     * Feature for system datasource name.
     */
    public static final String FEATURE_SYSTEM = PluginDbms.class.getName() + ".system";
    /**
     * The system datasource.
     */
    private String system = "org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TEST_INIT|sa|";
    /**
     * Feature for reference datasource name.
     */
    public static final String FEATURE_REFERENCE = PluginDbms.class.getName() + ".reference";
    /**
     * The guide datasource.
     */
    private String reference = "org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TEST_FINAL|sa|";

    /**
     * Get the system database name.
     * 
     * @return The system database name.
     */
    public String getSystem() {
        return system;
    }

    /**
     * Set the system database name.
     * 
     * @param system
     *            The database name.
     */
    @DontEval
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Get the reference database name.
     * 
     * @return The reference name.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Set the reference database name.
     * 
     * @param reference
     *            The reference.
     */
    @DontEval
    public void setReference(String reference) {
        this.reference = reference;
    }

    public PluginDbms() {
        IFeatureManager fm = SRServices.getFeatureManager();
        {
            // set loader.
            fm.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
            fm.add(PluginSchemaLoader.FEATURE_REUSE, true);
        }
        {
            // set schema.
            fm.add(PluginSchema.FEATURE_SOURCE, "/sgbd.cfg.xml");
            fm.add(PluginSchema.FEATURE_REUSE, true);
        }
        // set databases
        {
            fm.add(PluginDatabase.FEATURE_PROVIDER, DatabaseDefault.class.getName());
            fm.add(PluginDatabase.FEATURE_NAME, "systemDatabase;referenceDatabase");
            fm.add(PluginDatabase.FEATURE_REUSE, true);
        }
        {
            // set datasources
            fm.add(AbstractPluginDatabase.FEATURE_DATASOURCE, "systemConnection;referenceConnection");
            fm.add(AbstractPluginDatabase.FEATURE_DATABASE, "systemDatabase;referenceDatabase");
        }
        {
            // set comparator bases
            fm.add(PluginCompareBase.FEATURE_SYSTEM, "systemConnection");
            fm.add(PluginCompareBase.FEATURE_REFERENCE, "referenceConnection");
        }
        {
            // set release bases
            fm.add(PluginRelease.FEATURE_NAME, "systemDatabase;referenceDatabase");
        }
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SYSTEM, this);
        fm.set(FEATURE_REFERENCE, this);
        {
            // database schema
            add(new PluginSchemaLoader());
            add(new PluginSchema());
        }
        {
            // system base
            PluginConnection systemConnection = new PluginConnection();
            systemConnection.setConnection(system);
            systemConnection.setName("systemConnection");
            add(systemConnection);
            PluginDatabase systemDatabase = new PluginDatabase();
            systemDatabase.setName("systemDatabase");
            add(systemDatabase);
        }
        {
            // reference base
            PluginConnection referenceConnection = new PluginConnection();
            referenceConnection.setConnection(reference);
            referenceConnection.setName("referenceConnection");
            add(referenceConnection);
            PluginDatabase referenceDatabase = new PluginDatabase();
            referenceDatabase.setName("referenceDatabase");
            add(referenceDatabase);
        }
        {
            // drop both bases
            PluginScripts drop = new PluginScripts();
            drop.setClasspathrelative(true);
            drop.setValue("/sgbd_drop.sql");
            drop.setFailsafe(true);
            drop.setName("systemConnection;referenceConnection");
            add(drop);
        }
        {
            // create both bases
            PluginScripts create = new PluginScripts();
            create.setClasspathrelative(true);
            create.setValue("/sgbd_final.sql");
            create.setFailsafe(true);
            create.setName("systemConnection;referenceConnection");
            add(create);
        }
        {
            // apply filter on comparisons
            add(new PluginFilter());
        }
        // initialize after children add
        super.initialize(context);
    }
}