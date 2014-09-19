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
package org.specrunner.concordion;

import org.specrunner.SRServices;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.namespace.INamespaceProcessor;
import org.specrunner.transformer.ITransformer;
import org.specrunner.transformer.ITransformerGroup;
import org.specrunner.transformer.ITransformerManager;
import org.specrunner.transformer.core.TransformerGroupImpl;

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

    static {
        ITransformer old = SRServices.get(ITransformerManager.class).getDefault();
        ITransformerGroup group = new TransformerGroupImpl();
        group.add(old);
        group.add(new TransformerConcordion());
        SRServices.get(ITransformerManager.class).setDefault(group);
    }

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