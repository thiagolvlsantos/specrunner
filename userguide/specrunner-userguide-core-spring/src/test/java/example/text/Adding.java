package example.text;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.plugins.core.language.Sentence;
import org.specrunner.plugins.core.language.Synonyms;

import example.text.suv.Calc;

@RunWith(SRRunnerScenario.class)
@SRScenarioListeners({ ScenarioListenerPrint.class })
public class Adding {

    private Calc calc = new Calc();

    @Sentence("turn on")
    public void reset() {
        calc.clear();
    }

    @Sentence("entered (-?\\d+)")
    public void enter(int n) {
        calc.enter(n);
    }

    public void whenIPressAdd() {
        calc.add();
    }

    @Sentence("anything")
    @Synonyms({ "result should be (-?\\d+)" })
    public boolean result(Integer v) {
        Assert.assertEquals(v, calc.result());
        return true;
    }

    @Sentence("end")
    public void end() {
    }
}