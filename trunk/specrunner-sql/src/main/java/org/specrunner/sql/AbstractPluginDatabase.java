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
package org.specrunner.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Given a data source, a schema and a table, perform operations.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDatabase extends AbstractPluginTable {

    public static final String DATABASE_PROVIDER = "databaseProvider";
    /**
     * Prepared statements for input actions.
     */
    protected Map<String, PreparedStatement> inputs = new HashMap<String, PreparedStatement>();
    /**
     * Prepared statements for output actions.
     */
    protected Map<String, PreparedStatement> outputs = new HashMap<String, PreparedStatement>();
    /**
     * The schema name.
     */
    private String datasource;
    /**
     * The schema name.
     */
    private String schema;
    /**
     * The database action mode.
     */
    private EMode mode;
    /**
     * Default connection setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginConnection.class.getName() + ".reuse";
    /**
     * Set connection as reusable.
     */
    private Boolean reuse = false;

    /**
     * A mode is required.
     * 
     * @param mode
     *            A mode.
     */
    protected AbstractPluginDatabase(EMode mode) {
        this.mode = mode;
    }

    /**
     * Gets the datasource name.
     * 
     * @return The name.
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * Set the database name.
     * 
     * @param datasource
     *            A new datasource name.
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * Gets schema name.
     * 
     * @return The schema name.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the schema name.
     * 
     * @param schema
     *            A new schema name.
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Get the plugin mode.
     * 
     * @return The mode.
     */
    public EMode getMode() {
        return mode;
    }

    /**
     * Set the plugin mode.
     * 
     * @param mode
     *            A new mode.
     */
    public void setMode(EMode mode) {
        this.mode = mode;
    }

    public Boolean getReuse() {
        return reuse;
    }

    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        AbstractPluginDatabase plugin = this;
        final String currentName = getName() != null ? getName() : DATABASE_PROVIDER;
        IReuseManager rm = SpecRunnerServices.get(IReuseManager.class);
        if (reuse) {
            IReusable<?> ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("name", currentName);
                cfg.put("datasource", getDatasource());
                cfg.put("schema", getSchema());
                if (ir.canReuse(cfg)) {
                    ir.reset();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Reusing plugin database " + ir.getObject());
                    }
                    plugin = (AbstractPluginDatabase) ir.getObject();
                }
            } else {
                rm.put(currentName, new AbstractReusable<AbstractPluginDatabase>(currentName, this) {
                    @Override
                    public void reset() {
                    }

                    @Override
                    public void release() {
                        for (PreparedStatement ps : inputs.values()) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("Release: " + ps);
                            }
                            try {
                                if (!ps.isClosed()) {
                                    ps.close();
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("PluginDatabase " + this + " released.");
                        }
                    }

                    @Override
                    public boolean canReuse(Map<String, Object> cfg) {
                        boolean sameName = currentName.equalsIgnoreCase((String) cfg.get("name"));
                        boolean sameDatasource = datasource != null && datasource.equals(cfg.get("datasource"));
                        boolean sameSchema = schema != null && schema.equals(cfg.get("schema"));
                        return sameName || (sameDatasource && sameSchema);
                    }
                });
            }
        }

        IDataSourceProvider datasource = PluginConnection.getProvider(context, getDatasource());
        Schema schema = PluginSchema.getSchema(context, getSchema());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(getClass().getSimpleName() + " datasource:" + datasource);
            UtilLog.LOG.debug(getClass().getSimpleName() + "     schema:" + schema);
        }
        DataSource ds = datasource.getDataSource();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(getClass().getSimpleName() + " connection:" + connection);
            }
            plugin.perform(context, result, tableAdapter, connection, schema);
        } catch (SQLException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.commit();
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
        }
        result.addResult(Success.INSTANCE, context.peek());
        return ENext.DEEP;
    }

    public void perform(IContext context, IResultSet result, TableAdapter tableAdapter, Connection con, Schema schema) throws PluginException {
        List<CellAdapter> captions = tableAdapter.getCaptions();
        if (captions.isEmpty()) {
            throw new PluginException("Tables must have a caption.");
        }
        String tAlias = captions.get(0).getValue();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new PluginException("Table with alias '" + tAlias + "' not found in:" + schema.getAliasToTables().keySet());
        }
        List<RowAdapter> rows = tableAdapter.getRows();
        // headers are in the first row.
        RowAdapter header = rows.get(0);
        List<CellAdapter> headers = header.getCells();
        Column[] columns = new Column[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            String cAlias = headers.get(i).getValue();
            columns[i] = table.getAlias(cAlias);
            if (i > 0 && columns[i] == null) {
                throw new PluginException("Column with alias '" + cAlias + "' not found in:" + table.getAliasToColumns().keySet());
            }
        }
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter row = rows.get(i);
            List<CellAdapter> tds = row.getCells();
            if (tds.isEmpty()) {
                throw new PluginException("Empty lines are useless. " + row.getValue());
            }
            String type = tds.get(0).getValue();
            CommandType ct = CommandType.get(type);
            if (ct == null) {
                throw new PluginException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            Set<Value> values = new TreeSet<Value>();
            for (int j = 1; j < tds.size(); j++) {
                Column c = columns[j];
                CellAdapter td = tds.get(j);
                String att = td.getAttribute("title");
                String value = att != null ? att : td.getValue();

                IConverter converter = c.getConverter();
                String strConverter = td.getAttribute("converter");
                if (strConverter != null) {
                    IConverterManager cm = SpecRunnerServices.get(IConverterManager.class);
                    IConverter instance = cm.get(converter);
                    if (instance == null) {
                        try {
                            converter = (IConverter) Class.forName(strConverter).newInstance();
                            cm.bind(strConverter, converter);
                        } catch (Exception e) {
                            throw new PluginException(e);
                        }
                    }
                }
                List<String> args = new LinkedList<String>();
                int index = 0;
                String arg = td.getAttribute("arg" + (index++));
                while (arg != null) {
                    args.add(arg);
                    arg = td.getAttribute("arg" + (index++));
                }
                if (converter.accept(value)) {
                    Object obj;
                    try {
                        obj = converter.convert(value, args.isEmpty() ? null : args.toArray());
                    } catch (ConverterException e) {
                        result.addResult(Failure.INSTANCE, context.newBlock(td.getElement(), this), "Convertion error at row: " + i + ", cell: " + j + ". Attempt to convert '" + value + "' using a '" + converter + "'.");
                        continue;
                    }
                    if (obj == null && ct == CommandType.INSERT) {
                        obj = c.getDefaultValue();
                    }
                    IComparator comparator = c.getComparator();
                    String strComparator = td.getAttribute("comparator");
                    if (strComparator != null) {
                        IComparatorManager cm = SpecRunnerServices.get(IComparatorManager.class);
                        IComparator instance = cm.get(converter);
                        if (instance == null) {
                            try {
                                comparator = (IComparator) Class.forName(strComparator).newInstance();
                                cm.bind(strComparator, comparator);
                            } catch (Exception e) {
                                throw new PluginException(e);
                            }
                        }
                    }
                    Value v = new Value(c, td, obj, comparator);
                    values.add(v);
                    filled.put(c.getName(), v);
                }
            }
            try {
                switch (ct) {
                case INSERT:
                    if (mode == EMode.INPUT) {
                        performInsert(context, result, con, table, values, filled);
                    } else {
                        performSelect(context, result, con, table, values, filled, 1);
                    }
                    break;
                case UPDATE:
                    if (mode == EMode.INPUT) {
                        performUpdate(context, result, con, table, values);
                    } else {
                        performSelect(context, result, con, table, values, filled, 1);
                    }
                    break;
                case DELETE:
                    if (mode == EMode.INPUT) {
                        performDelete(context, result, con, table, values);
                    } else {
                        performSelect(context, result, con, table, values, filled, 0);
                    }
                    break;
                default:
                    throw new PluginException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
                }
            } catch (PluginException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(row.getElement(), this), e.getMessage());
            }
        }
        try {
            con.commit();
        } catch (SQLException e) {
            throw new PluginException(e);
        }
    }

    protected void performInsert(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, Map<String, Value> filled) throws PluginException {
        for (Column c : table.getAliasToColumns().values()) {
            if (filled.get(c.getName()) == null && c.getDefaultValue() != null) {
                values.add(new Value(c, null, c.getDefaultValue(), c.getComparator()));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table.getSchema().getName() + "." + table.getName() + " (");
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value e : values) {
            indexes.put(e.getColumn().getName(), i++);
            sbVal.append(e.getColumn().getName() + ",");
            sbPla.append("?,");
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > 1) {
            sbPla.setLength(sbPla.length() - 1);
        }
        sb.append(sbVal);
        sb.append(") values (");
        sb.append(sbPla);
        sb.append(")");
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    protected void performUpdate(IContext context, IResultSet result, Connection con, Table table, Set<Value> values) throws PluginException {
        StringBuilder sb = new StringBuilder();
        sb.append("update " + table.getSchema().getName() + "." + table.getName() + " set ");
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value v : values) {
            if (!v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbVal.append(v.getColumn().getName() + " = ?,");
            }
        }
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        sb.append(sbVal);
        sb.append(" where (");
        sb.append(sbPla);
        sb.append(")");
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    protected void performDelete(IContext context, IResultSet result, Connection con, Table table, Set<Value> values) throws PluginException {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from " + table.getSchema().getName() + "." + table.getName() + " where ");
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        sb.append(sbPla);
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    protected void performIn(IContext context, IResultSet result, Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) throws PluginException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values);
        }
        try {
            PreparedStatement pstmt = inputs.get(sql);
            if (pstmt == null) {
                pstmt = con.prepareStatement(sql);
                inputs.put(sql, pstmt);
            } else {
                pstmt.clearParameters();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("REUSE:" + pstmt);
                }
            }
            for (Value v : values) {
                Integer index = indexes.get(v.getColumn().getName());
                if (index != null) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("SET(" + index + ")=" + v.getValue());
                    }
                    pstmt.setObject(index, v.getValue());
                }
            }
            int count = pstmt.executeUpdate();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("[" + count + "]=" + sql);
            }
            if (expectedCount != count) {
                throw new PluginException("The expected update count (" + expectedCount + ") does not match, received = " + count + ".");
            }
        } catch (SQLException e) {
            throw new PluginException(e);
        }
    }

    protected void performSelect(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, Map<String, Value> filled, int expectedCount) throws PluginException {
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value v : values) {
            if (!v.getColumn().isKey()) {
                sbVal.append(v.getColumn().getName() + ",");
            }
        }
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(sbVal);
        sb.append(" from " + table.getSchema().getName() + "." + table.getName());
        sb.append(" where ");
        sb.append(sbPla);
        performOut(context, result, con, sb.toString(), indexes, values, expectedCount);
    }

    protected void performOut(IContext context, IResultSet result, Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) throws PluginException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values + ". indexes = " + indexes);
        }
        try {
            PreparedStatement pstmt = inputs.get(sql);
            if (pstmt == null) {
                pstmt = con.prepareStatement(sql);
                inputs.put(sql, pstmt);
            } else {
                pstmt.clearParameters();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("REUSE:" + pstmt);
                }
            }
            for (Value v : values) {
                Integer index = indexes.get(v.getColumn().getName());
                if (index != null) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("SET(" + index + ")=" + v.getValue());
                    }
                    pstmt.setObject(index, v.getValue());
                }
            }
            ResultSet rs = null;
            try {
                rs = pstmt.executeQuery();
                if (expectedCount == 1) {
                    if (!rs.next()) {
                        throw new PluginException("None register found with the given conditions: " + sql + "[" + values + "]");
                    }
                    for (Value v : values) {
                        Integer index = indexes.get(v.getColumn().getName());
                        if (index == null) {
                            IComparator comparator = v.getComparator();
                            Object received = rs.getObject(v.getColumn().getName());
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("CHECK(" + v.getValue() + ") = " + received);
                            }
                            if (!comparator.match(v.getValue(), received)) {
                                IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(v.getValue()), String.valueOf(received));
                                result.addResult(Failure.INSTANCE, context.newBlock(v.getCell().getElement(), this), new DefaultAlignmentException(aligner));
                            }
                        }
                    }
                    if (rs.next()) {
                        throw new PluginException("More than one register satisfy the condition: " + sql + "[" + values + "]");
                    }
                } else {
                    if (rs.next()) {
                        throw new PluginException("A result for " + sql + "[" + values + "] was not expected.");
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (SQLException e) {
            throw new PluginException(e);
        }
    }

}