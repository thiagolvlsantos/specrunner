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
package org.specrunner.runner;

import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;

/**
 * Filter a block on runner.
 * 
 * @author Thiago Santos
 * 
 */
public interface IBlockFilter {

    /**
     * Inicialize filter.
     * 
     * @param context
     *            The context.
     */
    void initialize(IContext context);

    /**
     * Check if a given block is accepted.
     * 
     * @param block
     *            The block to test.
     * @return <code>true</code>, if block is accepted, <code>false</code>,
     *         otherwise.
     */
    boolean accept(IBlock block);

    /**
     * Indicates to show message if not accepted.
     * 
     * @param block
     *            The block.
     * @return <code>true</code>, if block must show message, <code>false</code>
     *         , otherwise.
     */
    boolean showMessage(IBlock block);
}
