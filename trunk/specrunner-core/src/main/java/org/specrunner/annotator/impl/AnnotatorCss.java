/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.annotator.impl;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilNode;

/**
 * Add CSS style related to result status. For each result node add the
 * corresponding CSS class to the element.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorCss implements IAnnotator {

    @Override
    public void annotate(IResultSet result) throws AnnotatorException {
        for (IResult r : result) {
            IBlock block = r.getBlock();
            if (block.hasNode()) {
                UtilNode.appendCss(block.getNode(), r.getStatus().getCssName());
                UtilNode.appendCss(block.getNode(), r.getActionType().getCssName());
            }
        }
    }
}