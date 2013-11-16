package example.concordion;

import org.junit.BeforeClass;
import org.specrunner.SRServices;
import org.specrunner.transformer.ITransformer;

import example.concordion.transformer.TransformerConcordion;

public class TestConcordion {

    @BeforeClass
    public static void before() throws InstantiationException, IllegalAccessException {
        SRServices.get().bind(ITransformer.class, new TransformerConcordion());
    }
}