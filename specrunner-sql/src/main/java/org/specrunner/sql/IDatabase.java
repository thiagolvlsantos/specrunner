package org.specrunner.sql;

import java.sql.Connection;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.meta.Schema;
import org.specrunner.util.xom.TableAdapter;

public interface IDatabase {

    void perform(IPlugin plugin, IContext context, IResultSet result, TableAdapter tableAdapter, Connection con, Schema schema, EMode mode) throws PluginException;

    void release() throws PluginException;
}
