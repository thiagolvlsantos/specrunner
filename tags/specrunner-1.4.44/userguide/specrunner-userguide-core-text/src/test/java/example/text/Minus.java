package example.text;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.language.Sentence;
import org.specrunner.plugins.core.language.Synonyms;

import example.text.suv.Calc;

@RunWith(SRRunner.class)
public class Minus {

    private Calc calc = new Calc();

    @Sentence("ligada e limpa")
    public void reset() {
        calc.clear();
    }

    @Sentence("eu digito (-?\\d+)")
    @Synonyms({ "eu informo (-?\\d+)", "eu coloco (-?\\d+)" })
    public void enter(int n) {
        calc.enter(n);
    }

    @Sentence("escolho (.+)")
    @Synonyms({ "seleciono (.+)" })
    public void action(String ac) {
        calc.subtract();
    }

    @Sentence("resultado é (-?\\d+)")
    @Synonyms({ "aparece (-?\\d+)" })
    public boolean result(Integer v) {
        Assert.assertEquals(v, calc.result());
        return true;
    }
}
