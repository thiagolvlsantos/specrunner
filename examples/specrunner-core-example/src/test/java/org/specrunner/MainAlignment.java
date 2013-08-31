package org.specrunner;

import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.xom.IPresentation;

public final class MainAlignment {

    private MainAlignment() {
    }

    public static void main(String[] args) throws Exception {
        IPresentation e = new DefaultAlignmentException("Thigo Luiz Test", "Thiago do Luiz Teste");
        System.out.println(e);
        System.out.println(e.asNode().toXML());
    }
}
