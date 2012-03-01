/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.result;

import java.util.List;

import org.specrunner.context.IBlock;
import org.specrunner.util.IPresentation;

/**
 * A result set.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResultSet extends IStatus, Iterable<IResult>, IPresentation {

    /**
     * Lists status in result.
     * 
     * @return An iterator of status.
     */
    <T extends Status> Iterable<T> availableStatus();

    /**
     * Lists error status in result.
     * 
     * @return An iterator of status of type error in result.
     */
    <T extends Status> List<T> errorStatus();

    /**
     * Filters result by status type.
     * 
     * @param status
     *            The filter.
     * @return The subset of status.
     */
    <T extends Status> List<IResult> filterByStatus(T status);

    /**
     * Counts the status of a given type.
     * 
     * @param status
     *            The filter.
     * @return The number of result.
     */
    <T extends Status> int countStatus(T status);

    /**
     * Add a result.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source);

    /**
     * Adds a result.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param writable
     *            A writable information.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source, IWritable writable);

    /**
     * Adds a result with a message.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param message
     *            The message.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source, String message);

    /**
     * Adds a result with a message and a writable information.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param message
     *            The message.
     * @param writable
     *            The extra information.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source, String message, IWritable writable);

    /**
     * Adds a result with failure information.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param failure
     *            The failure.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source, Throwable failure);

    /**
     * Adds a result with failure and a writable information.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param failure
     *            The failure.
     * @param writable
     *            The extra information.
     * @return The result.
     */
    IResult addResult(Status status, IBlock source, Throwable failure, IWritable writable);
}