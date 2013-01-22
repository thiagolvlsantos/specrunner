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

import org.specrunner.context.IBlock;
import org.specrunner.plugins.IActionType;
import org.specrunner.util.xom.IPresentation;

/**
 * A individual result.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResult extends IStatus, IActionType, IPresentation {

    /**
     * If result has a block.
     * 
     * @return true, if it has a non null value, false, otherwise.
     */
    boolean hasBlock();

    /**
     * Block related to this result.
     * 
     * @return The node.
     */
    IBlock getBlock();

    /**
     * If result has a message.
     * 
     * @return true, if it has a non null value, false, otherwise.
     */
    boolean hasMessage();

    /**
     * Message associated to the result.
     * 
     * @return A message, or null, if no message has been set.
     */
    String getMessage();

    /**
     * If result has a failure.
     * 
     * @return true, if it has a non null value, false, otherwise.
     */
    boolean hasFailure();

    /**
     * In case of errors result, an exception hold its information.
     * 
     * @return An error if exists, null, otherwise.
     */
    Throwable getFailure();

    /**
     * If result has a writable.
     * 
     * @return true, if it has a non null value, false, otherwise.
     */
    boolean hasWritable();

    /**
     * Each result can have its extra information, in the form of a writable
     * object, in other words, a object that know how to write itself.
     * 
     * @return The writable, if exists, null, otherwise.
     */
    IWritable getWritable();
}