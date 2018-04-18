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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICacheCleaner;

/**
 * Cleaner of prepared statements.
 * 
 * @author Thiago Santos
 * 
 */
public class PreparedStatementCleaner implements ICacheCleaner<PreparedStatement> {

    /**
     * A shared instance of cleaner.
     */
    public static final ThreadLocal<PreparedStatementCleaner> INSTANCE = new ThreadLocal<PreparedStatementCleaner>() {
        protected PreparedStatementCleaner initialValue() {
            return new PreparedStatementCleaner();
        };
    };

    @Override
    public void destroy(PreparedStatement obj) {
        if (obj != null) {
            try {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Closing: " + obj);
                }
                if (!obj.isClosed()) {
                    obj.close();
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }
}
