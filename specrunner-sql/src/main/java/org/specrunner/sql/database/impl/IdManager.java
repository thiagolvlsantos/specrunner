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
package org.specrunner.sql.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.converters.ConverterException;
import org.specrunner.sql.database.CommandType;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.IRegister;
import org.specrunner.sql.database.IStatementFactory;
import org.specrunner.sql.database.SqlWrapper;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;
import org.specrunner.util.UtilLog;

/**
 * Manage tables and columns key fields.
 * 
 * @author Thiago Santos.
 * 
 */
public class IdManager {

    /**
     * Separator of virtual keys.
     */
    private static final String VIRTUAL_SEPARATOR = ";";

    /**
     * Map of named registers in database. When a resource is included and
     * generate keys, these keys are hold in the format:
     * 
     * <pre>
     * &lt;Table alias&gt;.&ltreference fields separated by ';'&gt; -> generated ID
     * </pre>
     * 
     * in this table.
     */
    protected Map<String, Object> aliasToKeys = new HashMap<String, Object>();
    /**
     * Record the opposite side of generated IDs mappings. This is used to
     * provide better error messages in comparison failures. Its format is:
     * 
     * <pre>
     * &lt;Table alias&gt;.&ltgenerated ID&gt; -> reference fields separated by ';'
     * </pre>
     */
    protected Map<String, Object> keysToAlias = new HashMap<String, Object>();

    /**
     * Current alias under control.
     */
    private String alias;
    /**
     * Current key under control.
     */
    private String key;

    /**
     * Clear mappings.
     */
    public void clear() {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Cleanning map of virtual alias to IDs. Size before clean: " + (aliasToKeys.size()));
        }
        aliasToKeys.clear();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.info("Cleanning map of IDs to virtual alias. Size before clean: " + (keysToAlias.size()));
        }
        keysToAlias.clear();
    }

    /**
     * Get by virtual key.
     * 
     * @param table
     *            The table alias.
     * @param value
     *            The virtual value.
     * @return The mapping object.
     */
    public Object lookup(String table, Object value) {
        String alias = table + "." + value;
        Object obj = aliasToKeys.get(alias);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("'" + alias + "' recovered as " + obj);
        }
        return obj;
    }

    /**
     * Remove pair from mappings.
     * 
     * @param alias
     *            The key to remove.
     * @param key
     *            The value to remove.
     */
    public void remove(String alias, String key) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Removed alias (" + alias + ")");
            UtilLog.LOG.debug("Removed keys (" + key + ")");
        }
        aliasToKeys.remove(alias);
        keysToAlias.remove(key);
    }

    /**
     * Bind alias and key to generated value.
     * 
     * @param alias
     *            Object alias.
     * @param key
     *            Object key.
     * @param generated
     *            The generated key.
     */
    public void bind(String alias, String key, Object generated) {
        bind(alias, generated, key + "." + String.valueOf(generated), alias.substring(alias.indexOf('.') + 1));
    }

    /**
     * Bind values fully qualified.
     * 
     * @param alias
     *            The alias.
     * @param generated
     *            The generated/expected ids.
     * @param key
     *            The key value.
     * @param value
     *            The value.
     */
    public void bind(String alias, Object generated, String key, Object value) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Add name -> id: " + alias + " -> " + generated);
            UtilLog.LOG.debug("Add id -> name: " + key + " -> " + value);
        }
        aliasToKeys.put(alias, generated);
        keysToAlias.put(key, value);
    }

    /**
     * Clear local adjusts.
     */
    public void clearLocal() {
        alias = null;
        key = null;
    }

    /**
     * Add information to local variables.
     * 
     * @param table
     *            The table name.
     * @param content
     *            The content.
     */
    public void addLocal(String table, String content) {
        if (alias == null) {
            alias = table + "." + content;
        } else {
            alias += VIRTUAL_SEPARATOR + content;
        }
        if (key == null) {
            key = table;
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Column reference '" + key + "'.");
        }
    }

    /**
     * Bind local names to a the generated key.
     * 
     * @param generated
     *            The generated key.
     */
    public void bindLocal(Object generated) {
        bind(alias, key, generated);
    }

    /**
     * Remove local mappings.
     */
    public void removeLocal() {
        remove(alias, key + "." + String.valueOf(aliasToKeys.get(alias)));
    }

    /**
     * Find a value by its virtual reference.
     * 
     * @param con
     *            The connection.
     * @param column
     *            The value column.
     * @param value
     *            The current value.
     * @param outputs
     *            Map of output
     * @return The object to set in outer select.
     * @throws SQLException
     *             On SQL errors.
     * @throws DatabaseException
     *             On execution errors.
     */
    public Object findValue(Connection con, Column column, Object value, IStatementFactory outputs) throws SQLException, DatabaseException {
        String tableOrAlias = column.getTableOrAlias();
        String key = tableOrAlias + "." + value;
        Object result = lookup(tableOrAlias, value);
        if (result == null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Recover virtual key for (" + key + ")");
            }
            Schema schema = column.getParent().getParent();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Lookup in schema " + schema.getName());
            }
            Table table = schema.getAlias(tableOrAlias);
            if (table == null) {
                throw new DatabaseException("Virtual column '" + tableOrAlias + "' not found in schema " + schema.getName() + ". It must be a name in domain set of: " + schema.getAliasToTables());
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Lookup in table " + table.getName());
            }
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            List<Column> keys = table.getKeys();
            int i = 0;
            for (Column c : keys) {
                sb.append((i++ == 0 ? "" : ",") + c.getName());
            }
            List<Column> references = table.getReferences();
            if (UtilLog.LOG.isDebugEnabled()) {
                for (Column c : references) {
                    sb.append((i++ == 0 ? "" : ",") + c.getName());
                }
            }
            sb.append(" from ");
            sb.append(schema.getName() + "." + table.getName());
            sb.append(" where ");
            i = 0;
            for (Column c : references) {
                sb.append((i++ == 0 ? "" : " AND ") + c.getName() + (c.isDate() ? " between ? and ?" : " = ?"));
            }
            String sql = sb.toString();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Query for (" + value + "):" + sql);
            }
            PreparedStatement inPstmt = outputs.getOutput(sql);
            if (inPstmt == null) {
                inPstmt = con.prepareStatement(sql);
                outputs.putOutput(sql, inPstmt);
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("From cache:" + inPstmt);
                }
            }
            StringTokenizer st = new StringTokenizer(String.valueOf(value), VIRTUAL_SEPARATOR);
            i = 1;
            while (st.hasMoreTokens()) {
                Column reference = references.get(i - 1);
                String token = st.nextToken();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Convert(" + reference.getAlias() + "." + token + ")");
                }
                Object tmp = null;
                if (reference.isVirtual()) {
                    tmp = findValue(con, reference, token, outputs);
                } else {
                    try {
                        tmp = reference.getConverter().convert(token, reference.getArguments().toArray());
                    } catch (ConverterException e) {
                        throw new DatabaseException(e);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Converted to:" + tmp + " ." + (tmp != null ? tmp.getClass() : "null"));
                }
                if (reference.isDate()) {
                    IComparator comp = reference.getComparator();
                    if (!(comp instanceof ComparatorDate)) {
                        throw new DatabaseException("Date columns must have comparators of type 'date'. Current type:" + comp.getClass());
                    }
                    ComparatorDate comparator = (ComparatorDate) comp;
                    comparator.initialize();
                    Date dateBefore = new Date(((Date) tmp).getTime() - comparator.getTolerance());
                    Date dateAfter = new Date(((Date) tmp).getTime() + comparator.getTolerance());
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Date range in virtual lookup [" + dateBefore + " to " + dateAfter + "]");
                    }
                    inPstmt.setObject(i++, dateBefore);
                    inPstmt.setObject(i++, dateAfter);
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("findValue.SET(" + i + "," + reference.getAlias() + "," + reference.getName() + ") = " + tmp);
                    }
                    inPstmt.setObject(i++, tmp);
                }
            }
            ResultSet rs = null;
            try {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Query: " + inPstmt);
                }
                rs = inPstmt.executeQuery();
                while (rs.next()) {
                    for (Column c : keys) {
                        result = rs.getObject(c.getName());
                        String inverse = table.getAlias() + "." + result;
                        bind(key, result, inverse, value);
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        for (Column c : references) {
                            UtilLog.LOG.debug("Value for " + c.getName() + ": " + rs.getObject(c.getName()));
                        }
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Virtual key (" + key + ") replaced by '" + result + "'");
        }
        return result;
    }

    /**
     * Prepare update information.
     * 
     * @param con
     *            The connection.
     * @param table
     *            The table.
     * @param register
     *            The values.
     * @param outputs
     *            The output map.
     * @throws SQLException
     *             On reading errors.
     * @throws DatabaseException
     *             On errors.
     */
    public void prepareUpdate(Connection con, Table table, IRegister register, IStatementFactory outputs) throws SQLException, DatabaseException {
        DatabaseMetaData meta = con.getMetaData();
        if (meta.supportsGetGeneratedKeys() && hasKey()) {
            // TODO: ou fazer apenas as exclusões mínimas ou
            // deixar limpar tudo como abaixo.
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Mapping cleanup required on update.");
            }
            clear();

            // TODO: com a limpeza da tabela completa esse código não seria mais
            // necessário, a menos que seja feito um algoritmo onde se remova
            // pontualmente. (Vale a pena?)
            String alias = null;
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            int i = 0;
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isReference()) {
                    sb.append((i++ == 0 ? "" : ",") + column.getName());
                }
            }
            sb.append(" from " + table.getParent().getName() + "." + table.getName());
            sb.append(" where ");
            i = 0;
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isKey()) {
                    Object find = column.isVirtual() ? findValue(con, column, v.getValue(), outputs) : v.getValue();
                    sb.append((i++ == 0 ? "" : " AND ") + column.getName() + " = '" + find + "'");
                    if (alias == null) {
                        alias = table.getAlias() + "." + v.getValue();
                    }
                } else if (column.isReference()) {
                    sb.append((i++ == 0 ? "" : " AND ") + column.getName() + " = '" + v.getValue() + "'");
                    if (alias == null) {
                        alias = table.getAlias() + "." + v.getValue();
                    }
                }
            }
            Statement stmt = null;
            try {
                // TODO: trocar por um prepared statement
                stmt = con.createStatement();
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sb.toString());
                    String key = null;
                    if (rs.next()) {
                        for (Value v : register) {
                            Column column = v.getColumn();
                            if (column.isReference()) {
                                Object tmp = rs.getObject(column.getName());
                                if (key == null) {
                                    key = column.getParent().getAlias() + "." + tmp;
                                } else {
                                    key += VIRTUAL_SEPARATOR + tmp;
                                }
                            }
                        }
                    }
                    remove(alias, key);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }

    /**
     * Check if local keys is present.
     * 
     * @return true, if local key is not null, false, o otherwise.
     */
    public boolean hasKey() {
        return key != null;
    }

    /**
     * Read generated keys.
     * 
     * @param con
     *            The connection.
     * @param pstmt
     *            The statement.
     * @param wrapper
     *            The wrapper.
     * @param table
     *            The table.
     * @param register
     *            The values.
     * @throws SQLException
     *             On reading errors.
     */
    public void readKeys(Connection con, PreparedStatement pstmt, SqlWrapper wrapper, Table table, IRegister register) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        if (meta.supportsGetGeneratedKeys() && hasKey()) {
            ResultSet rs = null;
            try {
                rs = pstmt.getGeneratedKeys();
                ResultSetMetaData metaData = rs.getMetaData();
                boolean generated = false;
                while (rs.next()) {
                    generated = true;
                    for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
                        bindLocal(rs.getObject(j));
                    }
                }
                if (!generated) {
                    // if it came from a sequence
                    for (Value v : register) {
                        if (v.getColumn().isSequence()) {
                            bindLocal(v.getValue());
                            break;
                        }
                    }
                }
                if (wrapper.getType() == CommandType.DELETE) {
                    removeLocal();
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }
}
