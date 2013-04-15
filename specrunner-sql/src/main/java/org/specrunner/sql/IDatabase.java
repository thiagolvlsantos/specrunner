package org.specrunner.sql;

import java.sql.Connection;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.meta.Schema;
import org.specrunner.util.xom.TableAdapter;

/**
 * Abstraction for SQL executor.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IDatabase {

    /**
     * Perform some actions in a database.
     * 
     * @param source
     *            The source plugin.
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param table
     *            The specification table.
     * @param con
     *            The connection.
     * @param schema
     *            The database meta model.
     * @param mode
     *            The database mode (in - actions|out - asserts)
     * @throws PluginException
     *             On perfom erros.
     */
    void perform(IPlugin source, IContext context, IResultSet result, TableAdapter table, Connection con, Schema schema, EMode mode) throws PluginException;

    /**
     * Release database resources.
     * 
     * @throws PluginException
     *             On release errors.
     */
    void release() throws PluginException;
}
