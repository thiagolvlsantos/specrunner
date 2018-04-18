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
package org.specrunner.sql.database.impl;

import java.util.LinkedList;

import org.specrunner.context.IContext;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.UtilNames;
import org.specrunner.sql.meta.Value;
import org.specrunner.util.UtilLog;

/**
 * Basic instance of a register. The tree set is used to keep values column name
 * order and optimize cache of statements.
 * 
 * @author Thiago Santos.
 */
@SuppressWarnings("serial")
public class RegisterDefault extends LinkedList<Value> implements IRegister {

    /**
     * Parent table.
     */
    private Table parent;

    /**
     * Default constructor.
     * 
     * @param parent
     *            A table.
     */
    public RegisterDefault(Table parent) {
        this.parent = parent;
    }

    @Override
    public Table getParent() {
        return parent;
    }

    @Override
    public String getTableOrAlias(IContext context, Column column) throws DatabaseException {
        String alias = column.getTableOrAlias();
        String pointer = column.getPointer();
        if (pointer != null) {
            alias = null;
            for (Value vp : this) {
                if (pointer.equals(vp.getColumn().getAlias())) {
                    alias = UtilNames.normalize(vp.getCell().getValue(context));
                    break;
                }
            }
            if (alias == null) {
                throw new DatabaseException("The column '" + column.getTableOrAlias() + "' point to a non-existing column '" + pointer + "' of this table. Adjust attribute 'pointer' into database mapping file.");
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("pointer(" + pointer + ") -> " + alias);
            }
        }
        return alias;
    }

    @Override
    public Value getByName(String name) {
        for (Value v : this) {
            if (v.getColumn().getName().equalsIgnoreCase(name)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public Value getByAlias(String alias) {
        for (Value v : this) {
            if (v.getColumn().getAlias().equalsIgnoreCase(alias)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean hasKeys() {
        for (Value v : this) {
            if (v.getColumn().isKey()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasReferences() {
        for (Value v : this) {
            if (v.getColumn().isReference()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasKeysOrReferences() {
        for (Value v : this) {
            if (v.getColumn().isKey() || v.getColumn().isReference()) {
                return true;
            }
        }
        return false;
    }
}
