/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.sql.meta;

/**
 * Extension supposing a XML parser.
 * 
 * @author Thiago Santos
 */
// CHECKSTYLE:OFF
public interface ISchemaLoaderXML extends ISchemaLoader {
    // CHECKSTYLE:ON
    /**
     * Attribute 'name' mark.
     */
    String ATTR_NAME = "name";
    /**
     * Attribute 'alias' mark.
     */
    String ATTR_ALIAS = "alias";
    /**
     * Attribute 'key' mark.
     */
    String ATT_KEY = "key";
    /**
     * Attribute 'sequence' mark.
     */
    String ATT_SEQUENCE = "sequence";
    /**
     * Attribute 'date' mark.
     */
    String ATT_DATE = "date";
    /**
     * Attribute 'default' mark.
     */
    String ATT_DEFAULT = "default";
    /**
     * Attribute 'virtual' mark.
     */
    String ATT_VIRTUAL = "virtual";
    /**
     * Attribute 'reference' mark.
     */
    String ATT_REFERENCE = "reference";
    /**
     * Default value for boolean fields marks.
     */
    String DEFAULT_FALSE = "false";
}