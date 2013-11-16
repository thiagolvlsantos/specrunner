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
package example.concordion.transformer;

import org.specrunner.source.namespace.core.NamespaceProcessorGroupDefault;

/**
 * Concordion processor group.
 * 
 * @author Thiago Santos
 * 
 */
public class ConcordionProcessorGroup extends NamespaceProcessorGroupDefault {

    /**
     * Default constructor.
     */
    public ConcordionProcessorGroup() {
        // execute transform before set, it depends on it.
        add(new ConcordionExecute());
        add(new ConcordionSet());
        add(new ConcordionAssertEquals());
        add(new ConcordionVerifyRows());
        add(new ConcordionRun());
    }
}