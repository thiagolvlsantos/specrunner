/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.sql.database;

import java.sql.Connection;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.context.IDestructable;
import org.specrunner.expressions.EMode;
import org.specrunner.expressions.INullEmptyFeature;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.meta.Schema;
import org.specrunner.util.reset.IResetable;
import org.specrunner.util.xom.node.TableAdapter;

/**
 * Abstraction for SQL executor.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IDatabase extends INullEmptyFeature, IDatabaseReader, IResetable, IDestructable {

    /**
     * Feature for sequence provider instance.
     */
    String FEATURE_SEQUENCE_PROVIDER = IDatabase.class.getName() + ".sequenceProvider";

    /**
     * Feature for SQL wrapper factories.
     */
    String FEATURE_SQL_WRAPPER_FACTORY = IDatabase.class.getName() + ".sqlWrapperFactory";

    /**
     * Feature for SQL statement factories.
     */
    String FEATURE_STATEMENT_FACTORY = IDatabase.class.getName() + ".statementFactory";

    /**
     * Feature for object manager instance.
     */
    String FEATURE_ID_MANAGER = IDatabase.class.getName() + ".idManager";

    /**
     * Feature for database listeners.
     */
    String FEATURE_LISTENERS = IDatabase.class.getName() + ".listeners";

    /**
     * Feature for database SQL script optimization.
     */
    String FEATURE_REUSE_SCRIPTS = IDatabase.class.getName() + ".reuseScripts";

    /**
     * Set the sequence provider.
     * 
     * @param sequenceProvider
     *            A provider.
     */
    void setSequenceProvider(ISequenceProvider sequenceProvider);

    /**
     * Set SQL wrapper.
     * 
     * @param sqlWrapperFactory
     *            A factory.
     */
    void setSqlWrapperFactory(ISqlWrapperFactory sqlWrapperFactory);

    /**
     * Set statement factory.
     * 
     * @param statementFactory
     *            A factory.
     */
    void setStatementFactory(IStatementFactory statementFactory);

    /**
     * Set the manager.
     * 
     * @param idManager
     *            The manager.
     */
    void setIdManager(IIdManager idManager);

    /**
     * Set database listeners.
     * 
     * @param listeners
     *            A list of listeners.
     */
    void setListeners(List<IDatabaseListener> listeners);

    /**
     * Flag to set reuse scripts status. Default is 'false'.
     * 
     * @param reuseScripts
     *            Reuse scripts flag.
     */
    void setReuseScripts(Boolean reuseScripts);

    /**
     * Set database name.
     * 
     * @param name
     *            Name.
     */
    void setName(String name);

    /**
     * Get database name.
     * 
     * @return The database name.
     */
    String getName();

    /**
     * Perform some actions in a database.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param adapter
     *            The specification table.
     * @param connection
     *            The connection.
     * @param schema
     *            The database meta model.
     * @param mode
     *            The database mode (in - actions|out - asserts)
     * @throws DatabaseException
     *             On perform errors.
     */
    void perform(IContext context, IResultSet result, TableAdapter adapter, Connection connection, Schema schema, EMode mode) throws DatabaseException;

    /**
     * Release database resources.
     * 
     * @throws PluginException
     *             On release errors.
     */
    void release() throws PluginException;
}
