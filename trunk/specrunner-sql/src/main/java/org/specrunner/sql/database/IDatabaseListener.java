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
package org.specrunner.sql.database;

import org.specrunner.util.reset.IResetable;

/**
 * A database listener to perform actions on tables.
 * 
 * @author Thiago Santos
 */
public interface IDatabaseListener extends IResetable {

    /**
     * On table start processing.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    void onTableIn(DatabaseTableEvent event) throws DatabaseException;

    /**
     * On register input command.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On register errors.
     */
    void onRegisterIn(DatabaseRegisterEvent event) throws DatabaseException;

    /**
     * On register output command.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             O register errors.
     */
    void onRegisterOut(DatabaseRegisterEvent event) throws DatabaseException;

    /**
     * On table end processing.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    void onTableOut(DatabaseTableEvent event) throws DatabaseException;
}
