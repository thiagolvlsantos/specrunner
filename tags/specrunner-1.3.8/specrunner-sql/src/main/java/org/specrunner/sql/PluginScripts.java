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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.SRServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.source.ISource;
import org.specrunner.sql.util.StringUtil;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * This plugin specifies the scripts to be performed. Scripts are specification
 * relative, and multiple scripts can be added using multiple call or by giving
 * names separated by ';'. If you prefer different separators for scripts or
 * commands you can set 'scriptseparator', 'sqlseparator', and/or
 * 'nameseparator', or set FEATURE_SCRIPT_SEPARATOR, FEATURE_SQL_SEPARATOR or
 * FEATURE_NAME_SEPARATOR.
 * 
 * <p>
 * If scripts are expected to be relative to classpath, set 'classpathrelative'
 * attribute to true, or add the 'FEATURE_CLASSPATH_RELATIVE' to true.
 * </p>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginScripts extends AbstractPluginValue {

    /**
     * Cache of scripts.
     */
    protected static ThreadLocal<ICache<String, String>> cache = new ThreadLocal<ICache<String, String>>() {
        @Override
        protected ICache<String, String> initialValue() {
            return SRServices.get(ICacheFactory.class).newCache(PluginScripts.class.getName());
        };
    };

    /**
     * Sets the script separator on specification. Default is ";".
     */
    public static final String FEATURE_SCRIPT_SEPARATOR = PluginScripts.class.getName() + ".scriptseparator";
    /**
     * Separator of script names.
     */
    private String scriptseparator = ";";
    /**
     * List of scripts to be performed.
     */
    private URI[] scripts;

    /**
     * Sets the SQL command separator. Default is ";".
     */
    public static final String FEATURE_SQL_SEPARATOR = PluginScripts.class.getName() + ".sqlseparator";
    /**
     * The SQL comand separator.
     */
    private String sqlseparator = ";";

    /**
     * Feature for names separators.
     */
    public static final String FEATURE_NAME_SEPARATOR = PluginScripts.class.getName() + ".separator";
    /**
     * Default name separator.
     */
    public static final String DEFAULT_NAME_SEPARATOR = ";";
    /**
     * The name separator, default is ";".
     */
    private String nameseparator = DEFAULT_NAME_SEPARATOR;

    /**
     * Feature for classpath relative scripts.
     */
    public static final String FEATURE_CLASSPATH_RELATIVE = PluginScripts.class.getName() + ".classpath";
    /**
     * Default classpath relative.
     */
    public static final boolean DEFAULT_CLASSPATH_RELATIVE = false;
    /**
     * The name separator, default is ";".
     */
    private boolean classpathrelative = DEFAULT_CLASSPATH_RELATIVE;

    /**
     * Fail safe feature means that on command execution command errors will not
     * raise errors but warnings instead.
     */
    public static final String FEATURE_FAILSAFE = PluginScripts.class.getName() + ".failsafe";
    /**
     * Set fail safe state.
     */
    private Boolean failsafe = false;

    /**
     * List of scripts as URIs.
     * 
     * @return The script list.
     */
    public URI[] getScripts() {
        return scripts;
    }

    /**
     * Set scripts.
     * 
     * @param scripts
     *            The scripts.
     */
    public void setScripts(URI[] scripts) {
        this.scripts = scripts == null ? null : Arrays.copyOf(scripts, scripts.length);
    }

    /**
     * Return the script separator token.
     * 
     * @return The script separator.
     */
    public String getScriptseparator() {
        return scriptseparator;
    }

    /**
     * Set script separator.
     * 
     * @param scriptseparator
     *            The separator.
     */
    public void setScriptseparator(String scriptseparator) {
        this.scriptseparator = scriptseparator;
    }

    /**
     * The SQL commands separator.
     * 
     * @return The separator.
     */
    public String getSqlseparator() {
        return sqlseparator;
    }

    /**
     * Sets the SQL command separators.
     * 
     * @param sqlseparator
     *            Separator.
     */
    public void setSqlseparator(String sqlseparator) {
        this.sqlseparator = sqlseparator;
    }

    /**
     * Get the name separator.
     * 
     * @return The separator.
     */
    public String getNameseparator() {
        return nameseparator;
    }

    /**
     * Set the name separator.
     * 
     * @param nameseparator
     *            The separator.
     */
    public void setNameseparator(String nameseparator) {
        this.nameseparator = nameseparator;
    }

    /**
     * Get if script is classpath relative or not.
     * 
     * @return false, if not classpath relative, true, otherwise.
     */
    public boolean isClasspathrelative() {
        return classpathrelative;
    }

    /**
     * Set classpath relative script flag.
     * 
     * @param classpathrelative
     *            true, to set classpath relative, false, otherwise.
     */
    public void setClasspathrelative(boolean classpathrelative) {
        this.classpathrelative = classpathrelative;
    }

    /**
     * Set if commands execution should report errors or warning on command
     * execution failure.
     * 
     * @return True, if warnings are expected, false, otherwise.
     */
    public Boolean getFailsafe() {
        return failsafe;
    }

    /**
     * Set fail safe status.
     * 
     * @param failsafe
     *            Fail safe status.
     */
    public void setFailsafe(Boolean failsafe) {
        this.failsafe = failsafe;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SCRIPT_SEPARATOR, this);
        fm.set(FEATURE_SQL_SEPARATOR, this);
        fm.set(FEATURE_NAME_SEPARATOR, this);
        fm.set(FEATURE_CLASSPATH_RELATIVE, this);
        fm.set(FEATURE_FAILSAFE, this);
        setScripts(context);
    }

    /**
     * Set script, if necessary.
     * 
     * @param context
     *            The context.
     * @throws PluginException
     *             On error.
     */
    protected void setScripts(IContext context) throws PluginException {
        // script manually set should not be changed.
        if (scripts == null) {
            Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
            String value = String.valueOf(tmp);
            StringTokenizer st = new StringTokenizer(value, scriptseparator);
            List<URI> list = new LinkedList<URI>();
            ISource source = context.getCurrentSource();
            URI base = source.getURI();
            while (st.hasMoreTokens()) {
                String val = st.nextToken().trim();
                if (!val.isEmpty()) {
                    URI f = null;
                    if (classpathrelative) {
                        try {
                            URL resource = PluginScripts.class.getResource(val);
                            if (resource == null) {
                                throw new PluginException("Resource '" + val + "' not found on classpath.");
                            }
                            f = resource.toURI();
                        } catch (URISyntaxException e) {
                            String msg = "Could not find: " + val;
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(msg);
                            }
                            throw new PluginException(msg, e);
                        }
                    } else {
                        f = base.resolve(val);
                    }
                    list.add(f);
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("PluginScript scheduled:" + f);
                    }
                }
            }
            scripts = list.toArray(new URI[list.size()]);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        String[] sources = StringUtil.tokenize(getName() != null ? getName() : PluginConnection.DEFAULT_CONNECTION_NAME, nameseparator);
        int failure = 0;
        for (String source : sources) {
            IDataSourceProvider provider = PluginConnection.getProvider(context, source);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("PluginScript provider:" + provider);
            }
            DataSource ds = provider.getDataSource();
            Connection connection = null;
            try {
                connection = ds.getConnection();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("PluginScript connection:" + connection);
                }
                for (URI u : scripts) {
                    Reader reader = null;
                    String str = u.toString();
                    String script = cache.get().get(str);
                    boolean fromCache = script != null;
                    if (fromCache) {
                        reader = new StringReader(script);
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Script " + str + " reused.");
                        }
                    } else {
                        if (str != null && str.startsWith("file:")) {
                            File f = new File(str.replace("file:/", ""));
                            if (!f.exists()) {
                                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Script:" + f + " not found."));
                                failure++;
                            } else {
                                IBlock block = context.peek();
                                if (block.getNode() instanceof Element) {
                                    Element old = (Element) block.getNode();
                                    old.addAttribute(new Attribute("href", String.valueOf(f.toURI())));
                                }
                                try {
                                    reader = new FileReader(f);
                                } catch (FileNotFoundException e) {
                                    if (UtilLog.LOG.isDebugEnabled()) {
                                        UtilLog.LOG.debug(e.getMessage(), e);
                                    }
                                }
                            }
                        } else {
                            try {
                                URL url = u.toURL();
                                URLConnection con = url.openConnection();
                                reader = new InputStreamReader(con.getInputStream());
                            } catch (MalformedURLException e) {
                                if (UtilLog.LOG.isDebugEnabled()) {
                                    UtilLog.LOG.debug(e.getMessage(), e);
                                }
                                result.addResult(Failure.INSTANCE, context.peek(), e);
                                failure++;
                            } catch (IOException e) {
                                if (UtilLog.LOG.isDebugEnabled()) {
                                    UtilLog.LOG.debug(e.getMessage(), e);
                                }
                                result.addResult(Failure.INSTANCE, context.peek(), e);
                                failure++;
                            }
                        }
                    }
                    if (reader != null) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("PluginScript perform:" + u);
                        }
                        failure += perform(context, result, connection, str, reader, fromCache);
                    }
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                failure++;
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in connection:" + source + ". Error:" + e.getMessage(), e));
            } finally {
                try {
                    if (connection != null) {
                        connection.commit();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in connection:" + source + ". Error:" + e.getMessage(), e));
                }
            }
        }
        if (failure == 0) {
            result.addResult(Success.INSTANCE, context.peek());
        }
        return ENext.DEEP;
    }

    /**
     * Perform script commands.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param connection
     *            Database connection.
     * @param reference
     *            Script reference as string.
     * @param script
     *            Script content.
     * @param fromCache
     *            Indicates if reader comes from cache or not.
     * @return The number of errors.
     * @throws SQLException
     *             On SQL errors, only if fail safe is off.
     */
    protected int perform(IContext context, IResultSet result, Connection connection, String reference, Reader script, boolean fromCache) throws SQLException {
        int failures = 0;
        Statement stmt = null;
        BufferedReader br = null;
        try {
            stmt = connection.createStatement();
            try {
                StringBuilder command = new StringBuilder();
                StringBuilder full = new StringBuilder();
                br = new BufferedReader(script);
                String line = null;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.endsWith(sqlseparator)) {
                        command.append(" ");
                        command.append(line.substring(0, line.length() - 1));
                        line = "";
                    }
                    if (line.isEmpty() || line.startsWith("--")) {
                        if (command.length() > 0) {
                            String sql = command.toString();
                            failures += execute(stmt, sql, context, result);
                            if (!fromCache) {
                                full.append(sql);
                                full.append("\n\n");
                            }
                            command.setLength(0);
                        }
                        continue;
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Command  before: " + line);
                    }
                    line = UtilEvaluator.replace(line, context, true);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Command   after: " + line);
                    }
                    command.append(" " + line);
                }
                if (command.length() > 0) {
                    String sql = command.toString();
                    failures += execute(stmt, sql, context, result);
                    if (!fromCache) {
                        full.append(sql);
                        full.append("\n\n");
                    }
                }
                if (!fromCache) {
                    cache.get().put(reference, full.toString());
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Script '" + reference + "' added to cache '" + cache.get().getName() + "'.");
                }
            } catch (Exception e) {
                failures++;
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.peek(), e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                    }
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (script != null) {
                try {
                    script.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
        }
        return failures;
    }

    /**
     * Perform a command, and return the number of errors in execution.
     * 
     * @param stmt
     *            A statement.
     * @param sql
     *            A command.
     * @param context
     *            A text context.
     * @param result
     *            A result set.
     * @return The number of errors in execution.
     */
    protected int execute(Statement stmt, String sql, IContext context, IResultSet result) {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Command execute: " + sql);
        }
        try {
            stmt.executeUpdate(sql);
            return 0;
        } catch (SQLException e) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Command   error: " + e.getMessage());
            }
            if (!failsafe) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
                return 1;
            } else {
                result.addResult(Warning.INSTANCE, context.peek(), e);
                return 0;
            }
        }
    }
}