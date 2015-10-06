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
package org.specrunner.util.string.core;

import java.text.Normalizer;

import org.specrunner.util.string.IStringNormalizer;

/**
 * String normalizer default.
 * 
 * @author Thiago Santos
 */
public class StringNormalizerDefault implements IStringNormalizer {

    @Override
    public String normalize(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (!Character.isWhitespace(c) && Character.isDefined(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String camelCase(String text) {
        return camelCase(text, false);
    }

    @Override
    public String camelCase(String text, boolean changeFirst) {
        if (text == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean whitespace = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                whitespace = true;
                continue;
            }
            if (result.length() == 0 && Character.isJavaIdentifierStart(c)) {
                if (changeFirst) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
                whitespace = false;
                continue;
            }
            if (Character.isJavaIdentifierPart(c)) {
                if (whitespace) {
                    result.append(Character.toUpperCase(c));
                    whitespace = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return clean(result.toString());
    }

    @Override
    public String clean(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
