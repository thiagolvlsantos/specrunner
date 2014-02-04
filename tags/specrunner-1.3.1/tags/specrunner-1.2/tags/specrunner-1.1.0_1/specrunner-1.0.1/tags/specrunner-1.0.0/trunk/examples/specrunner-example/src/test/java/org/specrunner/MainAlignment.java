package org.specrunner;

import org.specrunner.util.IPresentation;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;

public final class MainAlignment {

    private MainAlignment() {
    }

    public static void main(String[] args) throws Exception {
        IStringAlignerFactory f = SpecRunnerServices.get(IStringAlignerFactory.class);
        IStringAligner s = f.align("Thigo Luiz Test", "Thiago do Luiz Teste");
        IPresentation e = new DefaultAlignmentException(s);
        System.out.println(e);
        System.out.println(e.asNode().toXML());
    }
}
