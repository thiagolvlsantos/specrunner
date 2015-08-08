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
package org.specrunner.util.expression.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.util.expression.IPlaceholder;
import org.specrunner.util.expression.IProcessor;
import org.specrunner.util.expression.ITextAnalyzer;
import org.specrunner.util.expression.TextAnalyzerException;

/**
 * Default analyzer implementation.
 * 
 * @author Thiago Santos
 */
public class TextAnalyzerDefault implements ITextAnalyzer {

    @Override
    public String replace(String content, IPlaceholder placeholder, IProcessor processor, IContext context, boolean silent) throws TextAnalyzerException {
        if (content == null) {
            return null;
        }
        if ("".equals(content)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int begin = 0;
        int start = content.indexOf(placeholder.getStart(), begin);
        int end = content.indexOf(placeholder.getEnd(), start + 1);
        boolean found = false;
        boolean escape = false;
        Map<String, Object> cache = new HashMap<String, Object>();
        while (start >= 0 && end > start) {
            found = true;
            escape = start > placeholder.getStart().length() && content.charAt(start - placeholder.getStart().length() + 1) == placeholder.getEscape();
            if (!escape) {
                String exp = content.substring(start + placeholder.getStart().length(), end);
                Object val = null;
                if (cache.containsKey(exp)) {
                    val = cache.get(exp);
                } else {
                    val = processor.process(exp, context, silent);
                    cache.put(exp, val);
                }
                if (!exp.equals(val)) {
                    sb.append(content.substring(begin, start));
                    sb.append(val);
                } else {
                    sb.append(content.substring(begin, end + placeholder.getEnd().length()));
                }
            } else {
                sb.append(content.substring(begin, start - 1));
                sb.append(content.substring(start, end + placeholder.getEnd().length()));
            }
            begin = end + placeholder.getEnd().length();
            start = content.indexOf(placeholder.getStart(), begin + 1);
            if (start != -1) {
                end = content.indexOf(placeholder.getEnd(), start + 1);
            }
        }
        if (found && end != -1) {
            sb.append(content.substring(end + 1));
        } else {
            if (begin == 0) {
                return content;
            }
            sb.append(content.substring(begin));
        }
        return sb.toString();
    }

    @Override
    public Object evaluate(String content, IPlaceholder placeholder, IProcessor processor, IContext context, boolean silent) throws TextAnalyzerException {
        return processor.process(replace(content, placeholder, processor, context, silent), context, silent);
    }
}