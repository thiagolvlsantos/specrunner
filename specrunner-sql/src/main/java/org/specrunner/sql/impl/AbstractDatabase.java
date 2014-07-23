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
package org.specrunner.sql.impl;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.DatabaseRegisterEvent;
import org.specrunner.sql.DatabaseTableEvent;
import org.specrunner.sql.IColumnReader;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.IDatabaseListener;
import org.specrunner.sql.INullEmptyHandler;
import org.specrunner.sql.ISequenceProvider;
import org.specrunner.sql.ISqlWrapperFactory;
import org.specrunner.sql.IStatementFactory;
import org.specrunner.util.collections.ReverseIterable;

/**
 * Partial implementation of databases.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public abstract class AbstractDatabase implements IDatabase {

    /**
     * A null/empty handler.
     */
    protected INullEmptyHandler nullEmptyHandler = new NullEmptyHandlerDefault();

    /**
     * Sequence next value generator.
     */
    protected ISequenceProvider sequenceProvider = new SequenceProviderDefault();

    /**
     * Recover object from a result set column to be compared against the
     * specification object.
     */
    protected IColumnReader columnReader = new ColumnReaderDefault();

    /**
     * Factory of SQLs.
     */
    protected ISqlWrapperFactory sqlWrapperFactory = new SqlWrapperFactoryDefault();

    /**
     * Factory of statements.
     */
    protected IStatementFactory statementFactory = new StatementFactoryDefault();

    /**
     * List of listeners.
     */
    protected List<IDatabaseListener> listeners = new LinkedList<IDatabaseListener>();

    @Override
    public void initialize() {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_NULL_EMPTY_HANDLER, this);
        fm.set(FEATURE_SEQUENCE_PROVIDER, this);
        fm.set(FEATURE_COLUMN_READER, this);
        fm.set(FEATURE_SQL_WRAPPER_FACTORY, this);
        fm.set(FEATURE_STATEMENT_FACTORY, this);
        fm.set(FEATURE_LISTENERS, this);
    }

    /**
     * Get the null/empty handler.
     * 
     * @return Current null/empty handler.
     */
    public INullEmptyHandler getNullEmptyHandler() {
        return nullEmptyHandler;
    }

    @Override
    public void setNullEmptyHandler(INullEmptyHandler nullEmptyHandler) {
        this.nullEmptyHandler = nullEmptyHandler;
    }

    /**
     * Get the sequence values provider.
     * 
     * @return The provider.
     */
    public ISequenceProvider getSequenceProvider() {
        return sequenceProvider;
    }

    @Override
    public void setSequenceProvider(ISequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }

    /**
     * Get current column reader.
     * 
     * @return The current reader.
     */
    public IColumnReader getColumnReader() {
        return columnReader;
    }

    @Override
    public void setColumnReader(IColumnReader columnReader) {
        this.columnReader = columnReader;
    }

    /**
     * Get the SQL wrapper factory.
     * 
     * @return The current factory.
     */
    public ISqlWrapperFactory getSqlWrapperFactory() {
        return sqlWrapperFactory;
    }

    @Override
    public void setSqlWrapperFactory(ISqlWrapperFactory sqlWrapperFactory) {
        this.sqlWrapperFactory = sqlWrapperFactory;
    }

    /**
     * Get statement factory.
     * 
     * @return The current factory.
     */
    public IStatementFactory getStatementFactory() {
        return statementFactory;
    }

    @Override
    public void setStatementFactory(IStatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    /**
     * Get listeners.
     * 
     * @return Listeners.
     */
    public List<IDatabaseListener> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(List<IDatabaseListener> listeners) {
        if (listeners == null) {
            throw new IllegalArgumentException("Listeners cannot be a null list.");
        }
        this.listeners = listeners;
    }

    /**
     * Fire initialize event.
     */
    protected void fireInitialize() {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.initialize();
            }
        }
    }

    /**
     * Fire table in event.
     * 
     * @param event
     *            Event.
     * @throws PluginException
     *             On processing errors.
     */
    protected void fireTableIn(DatabaseTableEvent event) throws PluginException {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.onTableIn(event);
            }
        }
    }

    /**
     * Fire register in event.
     * 
     * @param event
     *            Event.
     * @throws PluginException
     *             On processing errors.
     */
    protected void fireRegisterIn(DatabaseRegisterEvent event) throws PluginException {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.onRegisterIn(event);
            }
        }
    }

    /**
     * Fire register out event.
     * 
     * @param event
     *            Event.
     * @throws PluginException
     *             On processing errors.
     */
    protected void fireRegisterOut(DatabaseRegisterEvent event) throws PluginException {
        synchronized (listeners) {
            for (IDatabaseListener listener : new ReverseIterable<IDatabaseListener>(listeners)) {
                listener.onRegisterOut(event);
            }
        }
    }

    /**
     * Fire table out event.
     * 
     * @param event
     *            Event.
     * @throws PluginException
     *             On processing errors.
     */
    protected void fireTableOut(DatabaseTableEvent event) throws PluginException {
        synchronized (listeners) {
            for (IDatabaseListener listener : new ReverseIterable<IDatabaseListener>(listeners)) {
                listener.onTableOut(event);
            }
        }
    }
}