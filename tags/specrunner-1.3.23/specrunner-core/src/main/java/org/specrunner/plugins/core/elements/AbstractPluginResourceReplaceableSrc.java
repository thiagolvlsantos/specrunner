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
package org.specrunner.plugins.core.elements;

/**
 * Plugin to replace 'src' resources.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginResourceReplaceableSrc extends AbstractPluginResourceReplaceable {

    /**
     * The source.
     */
    private String src;

    /**
     * The source attribute.
     * 
     * @return The source.
     */
    public String getSrc() {
        return src;
    }

    /**
     * Set source object.
     * 
     * @param src
     *            The source.
     */
    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    protected String getReferenceName() {
        return "src";
    }

    @Override
    protected String getReferenceValue() {
        return src;
    }
}
