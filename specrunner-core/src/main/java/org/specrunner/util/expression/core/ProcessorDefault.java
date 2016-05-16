/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.util.expression.core;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.expressions.IExpression;
import org.specrunner.util.UtilLog;
import org.specrunner.util.expression.IProcessor;
import org.specrunner.util.expression.TextAnalyzerException;

/**
 * Default processor implementation.
 * 
 * @author Thiago Santos
 *
 */
public class ProcessorDefault implements IProcessor {

    @Override
    public Object process(String content, IContext context, boolean silent) throws TextAnalyzerException {
        if (content == null) {
            return null;
        }
        if ("".equals(content)) {
            return "";
        }
        try {
            IExpression expression = SRServices.getExpressionFactory().create(content, context);
            return expression.evaluate(context, silent);
        } catch (SpecRunnerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new TextAnalyzerException(e);
        }
    }
}
