/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.util.xom.IPresentation;

/**
 * A result set.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResultSet extends List<IResult>, IStatus, IPresentation {

    /**
     * Feature for full trace on results.
     */
    String FEATURE_FULL_TRACE = IResultSet.class.getName() + ".fullTrace";
    /**
     * Feature for result filter.
     */
    String FEATURE_RESULT_FILTER = IResultSet.class.getName() + ".resultFilter";

    /**
     * Feature for disable success recording.
     */
    String FEATURE_RECORD_SUCCESS = IResultSet.class.getName() + ".recordSuccess";

    /**
     * Feature for expected messages.
     */
    String FEATURE_EXPECTED_MESSAGES = IResultSet.class.getName() + ".messages";

    /**
     * Feature for messages expectation order.
     */
    String FEATURE_EXPECTED_SORTED = IResultSet.class.getName() + ".sorted";

    /**
     * Feature for messages criteria.
     */
    String FEATURE_EXPECTED_CRITERIA = IResultSet.class.getName() + ".criteria";

    /**
     * Set full trace on results dump.
     * 
     * @param fullTrace
     *            true, to show full trace, false, otherwise. Default is 'true'.
     */
    void setFullTrace(Boolean fullTrace);

    /**
     * Get full trace status.
     * 
     * @return true, if full trace is enabled, false, otherwise.
     */
    Boolean getFullTrace();

    /**
     * Set result filter.
     * 
     * @param resultFilter
     *            A filter.
     */
    void setResultFilter(IResultFilter resultFilter);

    /**
     * Return the filter.
     * 
     * @return The result filter.
     */
    IResultFilter getResultFilter();

    /**
     * Setting to record success action in result set.
     * 
     * @param recordSuccess
     *            The record success.
     */
    void setRecordSuccess(Boolean recordSuccess);

    /**
     * Flag to record status which <code>isError</code> returns true. Default is
     * <code>true</code>.
     * 
     * @return true, if record success flag is enabled, false, to record only
     *         error status.
     */
    Boolean getRecordSuccess();

    /**
     * Set expected messages.
     * 
     * @param expected
     *            Expected messages.
     */
    void setMessages(String[] expected);

    /**
     * Get the expected messages.
     * 
     * @return The messages.
     */
    String[] getMessages();

    /**
     * Set expected order of messages.
     * 
     * @param sorted
     *            true, if expected messages must obey the specification order,
     *            false, otherwise. Default might be false.
     */
    void setSorted(Boolean sorted);

    /**
     * Get the sort flag.
     * 
     * @return true, of order required, false, otherwise.
     */
    Boolean getSorted();

    /**
     * String comparison criteria for messages.
     * 
     * @return Criteria.
     */
    IStringTest getCriteria();

    /**
     * Set a string comparison criteria for messages.
     * 
     * @param criteria
     *            A criteria.
     */
    void setCriteria(IStringTest criteria);

    /**
     * Consolidate expectations and received errors.
     * 
     * @param context
     *            The context.
     */
    void consolidate(IContext context);

    /**
     * Lists status in result.
     * 
     * @param <T>
     *            A subclass of <code>Status</code>.
     * @return An iterator of status.
     */
    <T extends Status> List<T> availableStatus();

    /**
     * Lists error status in result.
     * 
     * @param <T>
     *            A subclass of <code>Status</code>.
     * @return An iterator of status of type error in result.
     */
    <T extends Status> List<T> errorStatus();

    /**
     * Filters result by status type.
     * 
     * @param <T>
     *            A subclass of <code>Status</code>.
     * @param status
     *            The filters.
     * @return The subset of status.
     */
    <T extends Status> List<IResult> filterByStatus(T... status);

    /**
     * Filters result by status type.
     * 
     * @param <T>
     *            A subtype of Status.
     * @param start
     *            Range start.
     * @param end
     *            Range end.
     * @param status
     *            The filters.
     * @return The subset of status.
     */
    <T extends Status> List<IResult> filterByStatus(int start, int end, T... status);

    /**
     * Filters result by status type.
     * 
     * @param <T>
     *            A subtype of Status.
     * @param subset
     *            Subset list.
     * @param status
     *            The filters.
     * @return The subset of status.
     */
    <T extends Status> List<IResult> filterByStatus(List<IResult> subset, T... status);

    /**
     * Return number of errors.
     * 
     * @return The errors number.
     */
    int countErrors();

    /**
     * Return number of errors.
     * 
     * @param start
     *            Start index.
     * @return The errors number starting from a index position.
     */
    int countErrors(int start);

    /**
     * Counts the status of a given type.
     * 
     * @param <T>
     *            A subtype of <code>Status</code>.
     * @param status
     *            The filters.
     * @return The number of result.
     */
    <T extends Status> int countStatus(T... status);

    /**
     * Counts the status of a given type.
     * 
     * @param start
     *            Range start.
     * @param end
     *            Range end.
     * @param <T>
     *            A status class.
     * @param status
     *            The filters.
     * @return The number of result.
     */
    <T extends Status> int countStatus(int start, int end, T... status);

    /**
     * Counts the status of a given type.
     * 
     * @param subset
     *            A subset list.
     * @param <T>
     *            A status class.
     * @param status
     *            The filters.
     * @return The number of result.
     */
    <T extends Status> int countStatus(List<IResult> subset, T... status);

    /**
     * List action types available for all results.
     * 
     * @return The list of types.
     */
    List<ActionType> actionTypes();

    /**
     * List action types available for the result subset.
     * 
     * @param subset
     *            Subset to be analyzed.
     * @return The list of types.
     */
    List<ActionType> actionTypes(List<IResult> subset);

    /**
     * Filter the result itself by <code>ActionType</code>.
     * 
     * @param actionType
     *            The action types.
     * @return The filtered subset.
     */
    List<IResult> filterByType(ActionType... actionType);

    /**
     * Filter the subset by <code>ActionType</code>.
     * 
     * @param subset
     *            The subset to be filtered.
     * @param actionType
     *            The action types.
     * @return The filtered subset.
     */
    List<IResult> filterByType(List<IResult> subset, ActionType... actionType);

    /**
     * Count results of a given set of action types.
     * 
     * @param actionType
     *            The action types.
     * @return The size of the filtered set.
     */
    int countType(ActionType... actionType);

    /**
     * Count results of a given type in a list.
     * 
     * @param subset
     *            A subset of results.
     * @param actionType
     *            The action types.
     * @return The size of the filtered set.
     */
    int countType(List<IResult> subset, ActionType... actionType);

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

    /**
     * Get a subset of result as a result set itself.
     * 
     * @param start
     *            The start index.
     * @param end
     *            The end index.
     * @return The corresponding subset.
     */
    IResultSet subSet(int start, int end);
}
