package example.concordion;

import org.junit.BeforeClass;
import org.specrunner.SRServices;
import org.specrunner.transformer.ITransformer;

public class Concordion {

    @BeforeClass
    public static void before() {
        SRServices.get().bind(ITransformer.class, new TransformerConcordion());
    }
}