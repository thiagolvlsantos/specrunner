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
package org.specrunner.annotator.core;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.annotator.IAnnotatorFactoryGroup;
import org.specrunner.annotator.IAnnotatorGroup;
import org.specrunner.util.composite.core.CompositeImpl;

/**
 * Default annotator factory group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorFactoryGroupImpl extends CompositeImpl<IAnnotatorFactoryGroup, IAnnotatorFactory> implements IAnnotatorFactoryGroup {

    @Override
    public IAnnotator newAnnotator() throws AnnotatorException {
        IAnnotatorGroup group = new AnnotatorGroupImpl();
        for (IAnnotatorFactory af : getChildren()) {
            IAnnotator an = af.newAnnotator();
            if (an != null) {
                group.add(an);
            }
        }
        return group;
    }
}
