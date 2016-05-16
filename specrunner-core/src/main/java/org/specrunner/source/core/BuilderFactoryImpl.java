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
package org.specrunner.source.core;

import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Builder;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.specrunner.source.IBuilderFactory;
import org.specrunner.source.SourceException;

/**
 * Default builder implementation.
 * 
 * @author Thiago Santos.
 * 
 */
public class BuilderFactoryImpl extends EncodedImpl implements IBuilderFactory {

    @Override
    public Builder newBuilder(Map<String, Object> properties) throws SourceException {
        try {
            // i've tried to use the same builder, but there is something
            // wrong with NekoHTML parser o reuse, leaving this way for while.
            return new Builder(getParser(properties), true);
        } catch (Exception e) {
            throw new SourceException(e);
        }
    }

    /**
     * Get the parser.
     * 
     * @param properties
     *            The properties.
     * @return A SaxParser.
     * @throws Exception
     *             On get parser errors.
     */
    protected AbstractSAXParser getParser(Map<String, Object> properties) throws Exception {
        AbstractSAXParser parser = new AbstractSAXParser(new HTMLConfiguration()) {
        };
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/override-namespaces", false);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", getEncoding());
        for (Entry<String, Object> e : properties.entrySet()) {
            if (e.getValue() instanceof Boolean) {
                parser.setFeature(e.getKey(), (Boolean) e.getValue());
            } else {
                parser.setProperty(e.getKey(), e.getValue());
            }
        }
        return parser;
    }

}
