/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.util.xom.node;

import nu.xom.Node;

import org.specrunner.util.expression.IAbstraction;

/**
 * Stands for any object which hold an element.
 * 
 * @author Thiago Santos
 * 
 */
public interface INodeHolder extends IAbstraction {

    /**
     * Attribute for property access.
     */
    String ATTRIBUTE_PROPERTY = "property";
    /**
     * Attribute to force evaluation using 'attribute value' or 'tag
     * getValue()'.
     */
    String ATTRIBUTE_FORCE_VALUE = "forceValue";
    /**
     * Attribute for value.
     */
    String ATTRIBUTE_VALUE = "value";
    /**
     * Attribute for arguments prefix.
     */
    String ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX = "arg";
    /**
     * Attribute for value evaluation.
     */
    String ATTRIBUTE_EVALUATION = "eval";
    /**
     * Attribute for reader.
     */
    String ATTRIBUTE_READER = "reader";
    /**
     * Attribute for converter.
     */
    String ATTRIBUTE_CONVERTER = "converter";
    /**
     * Attribute for formatter.
     */
    String ATTRIBUTE_FORMATTER = "formatter";
    /**
     * Attribute for arguments prefix.
     */
    String ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX = "for";
    /**
     * Attribute for comparator.
     */
    String ATTRIBUTE_COMPARATOR = "comparator";

    /**
     * Get the hold node.
     * 
     * @return The node.
     */
    Node getNode();

    /**
     * Set the hold node.
     * 
     * @param node
     *            The node.
     */
    void setNode(Node node);

    /**
     * Detaches the current node.
     */
    void detach();

    /**
     * Set attribute value to look for on <code>getObject(...)</code>
     * calls.Default is ATTRIBUTE_VALUE.
     * 
     * @return The attribute value holder.
     */
    String getAttributeValue();

    /**
     * Set value attribute holder.
     * 
     * @param attributeValue
     *            A attribute name.
     */
    void setAttributeValue(String attributeValue);

    /**
     * Get qualified name (include namespace).
     * 
     * @return The local name.
     */
    String getQualifiedName();

    /**
     * Check a tag name.
     * 
     * @param name
     *            The name.
     * @return true, if element name match, false, otherwise.
     */
    boolean hasName(String name);

    /**
     * Check if an given attribute is present.
     * 
     * @param name
     *            The attribute name.
     * @return true, if present, false, otherwise.
     */
    boolean hasAttribute(String name);

    /**
     * Get attribute value by type.
     * 
     * @param name
     *            The name.
     * @return The value, if exists, null, otherwise.
     */
    String getAttribute(String name);

    /**
     * Get attribute value by type.
     * 
     * @param name
     *            The name.
     * @param defaultValue
     *            The defaul value.
     * @return The value, if exists, null, otherwise.
     */
    String getAttribute(String name, String defaultValue);

    /**
     * Set attribute value.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    void setAttribute(String name, String value);

    /**
     * Remove a attribute.
     * 
     * @param name
     *            The attribute name.
     */
    void removeAttribute(String name);

    /**
     * Check if an attribute exists, and if it exists, if it contains the given
     * value.
     * 
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     * @return true, if attribute exists and if contains value, false,
     *         otherwise.
     */
    boolean attributeContains(String name, String value);

    /**
     * Check if an attribute exists, and if it exists, if it is equal to the
     * given value.
     * 
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     * @return true, if attribute exists and the value matches, false,
     *         otherwise.
     */
    boolean attributeEquals(String name, String value);

    /**
     * Set text to a node removing all children elements.
     * 
     * @param text
     *            The expected text.
     */
    void setValue(String text);

    /**
     * Prepend text to the node.
     * 
     * @param text
     *            A text.
     */
    void prepend(String text);

    /**
     * Prepend a note to the node.
     * 
     * @param node
     *            A node.
     */
    void prepend(Node node);

    /**
     * Append text to the node.
     * 
     * @param text
     *            A text.
     */
    void append(String text);

    /**
     * Append node to the node.
     * 
     * @param node
     *            A node.
     */
    void append(Node node);

    /**
     * The XML representation.
     * 
     * @return The XML version.
     */
    String toXML();
}