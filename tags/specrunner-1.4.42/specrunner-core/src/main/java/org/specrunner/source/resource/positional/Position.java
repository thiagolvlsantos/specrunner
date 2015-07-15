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
package org.specrunner.source.resource.positional;

/**
 * Stand for a position in the document where something can be added, i.e.,
 * //head.
 * 
 * @author Thiago Santos
 * 
 */
public final class Position {

    /**
     * The head position is given by this specification XPath.
     */
    public static final String HEAD = "//head";
    /**
     * The body position is given by this specification XPath.
     */
    public static final String BODY = "//body";
    /**
     * On header start.
     */
    public static final Position HEAD_START = new Position(HEAD, EPlace.START);
    /**
     * On header end.
     */
    public static final Position HEAD_END = new Position(HEAD, EPlace.END);
    /**
     * On body start.
     */
    public static final Position BODY_START = new Position(BODY, EPlace.START);
    /**
     * On body end.
     */
    public static final Position BODY_END = new Position(BODY, EPlace.END);

    /**
     * Position xpath.
     */
    private final String xpath;
    /**
     * Position placement.
     */
    private final EPlace place;

    /**
     * Creates a position based on XPath and placement.
     * 
     * @param xpath
     *            The XPath.
     * @param place
     *            The place.
     */
    private Position(String xpath, EPlace place) {
        this.xpath = xpath;
        this.place = place;
    }

    /**
     * The XPath reference.
     * 
     * @return The xpath.
     */
    public String getXpath() {
        return xpath;
    }

    /**
     * The placement element.
     * 
     * @return The placement.
     */
    public EPlace getPlace() {
        return place;
    }

    /**
     * Creates a new position based on XPath information.
     * 
     * @param xpath
     *            The XPath target.
     * @param place
     *            The place relative to the XPath.
     * @return A position which reflects the XPath and position.
     */
    public static Position newPosition(String xpath, EPlace place) {
        return new Position(xpath, place);
    }

    @Override
    public String toString() {
        return "(" + xpath + " to " + place + ")";
    }
}