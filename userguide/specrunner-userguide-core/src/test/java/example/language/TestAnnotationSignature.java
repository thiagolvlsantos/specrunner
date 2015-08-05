package example.language;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.converters.Converter;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.language.Placeholders;
import org.specrunner.plugins.core.language.Sentence;

@RunWith(SRRunner.class)
public class TestAnnotationSignature {

    static {
        Placeholders.get().put("$if", "(Yes|No)");
    }

    @Sentence("default of $int plus $quote plus $if")
    public void defaultCall(int a, String b, @Converter(name = "yesNo") boolean c) {
        Assert.assertEquals("10200true", a + b + c);
    }

    @Sentence("reflection of $a plus $hjhjh plus $if")
    public void reflectionCall(int a, String b, @Converter(name = "yesNo") boolean c) {
        Assert.assertEquals("10200true", a + b + c);
    }
}
