package example.text;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.AfterScenario;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunnerSpringScenario;
import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.plugins.core.language.Sentence;
import org.specrunner.plugins.core.language.Synonyms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import example.text.suv.Calc;

@RunWith(SRRunnerSpringScenario.class)
@ContextConfiguration(locations = { "/applicationContext-Calc.xml" })
@SRScenarioListeners({ ScenarioListenerPrint.class })
public class SpringAdding {

    @Autowired
    private Calc calc;

    @Configuration
    public void configure(IConfiguration cfg) {
        System.out.println("CONFIGURE...");
    }

    @Before
    public void clear() {
        System.out.println("CLEAN..." + System.currentTimeMillis());
    }

    @BeforeScenario
    public void clearScenario() {
        System.out.println("CLEAN SCENARIO..." + System.currentTimeMillis());
    }

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

    @After
    public void finish() {
        System.out.println("FINISH..." + System.currentTimeMillis());
    }

    @AfterScenario
    public void finishScenario() {
        System.out.println("FINISH SCENARIO..." + System.currentTimeMillis());
    }
}