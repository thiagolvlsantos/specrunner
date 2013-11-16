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

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.namespace.INamespaceProcessor;
import org.specrunner.transformer.ITransformer;

/**
 * Concordion transformer. Any file with:
 * <ul>
 * <li>concordion:set</li>
 * <li>concordion:assertEquals</li>
 * <li>concordion:execute</li>
 * <li>concordion:verifyRows</li>
 * <li>concordion:run</li>
 * </ul>
 * file can be read directly.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class TransformerConcordion implements ITransformer {

    @Override
    public void initialize() {
    }

    @Override
    public ISource transform(ISource source) throws SourceException {
        INamespaceProcessor processor = new ConcordionProcessorGroup();
        processor.process(source.getNamespaceInfo(), source.getDocument());
        return source;
    }
}