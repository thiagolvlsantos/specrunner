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
package org.specrunner.util.expression;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;

/**
 * Utility class for expressions.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilExpression {

    /**
     * Hidden constructor.
     */
    private UtilExpression() {
    }

    /**
     * Given a content, surround it with the expression mark.
     * 
     * @param content
     *            The content to become an expression.
     * @return The corresponding expression block.
     */
    public static String asExpression(String content) {
        IPlaceholder ph = SRServices.get(IPlaceholder.class);
        return ph.getStart() + content + ph.getEnd();
    }

    /**
     * Evaluates the text and returns an object.
     * 
     * @param text
     *            The text expression.
     * @param context
     *            A context.
     * @param silent
     *            To evaluate expressions silently.
     * @return The result.
     * @throws PluginException
     *             On evaluation errors.
     */
    public static Object evaluate(String text, IContext context, boolean silent) throws PluginException {
        IPlaceholder placeholder = SRServices.get(IPlaceholder.class);
        ITextAnalyzer analyser = SRServices.get(ITextAnalyzer.class);
        IProcessor processor = SRServices.get(IProcessor.class);
        return analyser.evaluate(text, placeholder, processor, context, silent);
    }

    /**
     * Replace placeholders in text.
     * 
     * @param text
     *            The text to be replaced.
     * @param context
     *            A context.
     * @param silent
     *            To evaluate expressions silently.
     * @return The replaced text.
     * @throws PluginException
     *             On replacing errors.
     */
    public static String replace(String text, IContext context, boolean silent) throws PluginException {
        IPlaceholder placeholder = SRServices.get(IPlaceholder.class);
        ITextAnalyzer analyser = SRServices.get(ITextAnalyzer.class);
        IProcessor processor = SRServices.get(IProcessor.class);
        return analyser.replace(text, placeholder, processor, context, silent);
    }
}
