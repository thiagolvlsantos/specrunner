package org.specrunner.source;

import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.transformer.ITransformer;
import org.specrunner.transformer.impl.TransformerGroupImpl;
import org.specrunner.transformer.impl.TransformerHref;
import org.specrunner.util.UtilIO;

public class TestCore extends TstPai {

    @Before
    public void antes() {
        SpecRunnerServices.get().bind(ITransformer.class, new TransformerGroupImpl().add(new TransformerBody()).add(new TransformerHref()));
    }

    @Test
    public void rodarTeste() throws Exception {
        runFile("core.html");
    }

    public static void main(String[] args) throws Exception {
        UtilIO.pressKey();
        for (int i = 0; i < 1000; i++) {
            new TestCore().rodarTeste();
        }
    }
}
