package example.normalizer;

import org.junit.Assert;
import org.junit.Test;
import org.specrunner.util.string.core.StringNormalizerDefault;

public class TestNormalizer {
    @Test
    public void run() {
        Assert.assertEquals("dateAny", new StringNormalizerDefault().camelCase("  Date any:"));
    }
}
