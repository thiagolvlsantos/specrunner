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
package org.specrunner.annotator.core;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.annotator.IAnnotatorFactory;

/**
 * Default factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorFactoryImpl implements IAnnotatorFactory {

    /**
     * Reused annotator.
     */
    protected IAnnotator annotator;

    /**
     * Creates a new annotator factory.
     * 
     * @param annotator
     *            The annotator.
     */
    public AnnotatorFactoryImpl(IAnnotator annotator) {
        setAnnotator(annotator);
    }

    /**
     * Gets the annotator.
     * 
     * @return The annotator.
     */
    public IAnnotator getAnnotator() {
        return annotator;
    }

    /**
     * Set the annotator.
     * 
     * @param annotator
     *            The annotator.
     */
    public void setAnnotator(IAnnotator annotator) {
        this.annotator = annotator;
    }

    @Override
    public IAnnotator newAnnotator() throws AnnotatorException {
        return getAnnotator();
    }
}
