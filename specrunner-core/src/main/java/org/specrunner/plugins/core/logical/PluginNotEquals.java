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
package org.specrunner.plugins.core.logical;

import org.specrunner.SpecRunnerException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;

/**
 * The dual of equals.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginNotEquals extends PluginEquals {

    @Override
    protected boolean verify(IContext context, IComparator comparator, Object reference, Object value) throws SpecRunnerException {
        boolean result = !super.verify(context, comparator, reference, value);
        if (!result) {
            error = new PluginException("Values are equals.");
        }
        return result;
    }

}
