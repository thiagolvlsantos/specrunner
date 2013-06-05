package example.sql;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerSpring;
import org.springframework.test.context.TestExecutionListeners;

@RunWith(SRRunnerSpring.class)
@TestExecutionListeners(inheritListeners = false, listeners = { PrintListener.class })
public class TestSRSpring {

    public List<Qualquer> lista1() {
        return Arrays.asList(new Qualquer(1, "Eu"), new Qualquer(2, "Tu"));
    }

    public List<Qualquer> lista2() {
        return Arrays.asList(new Qualquer(1, "Eu"));
    }

    public List<Qualquer> lista3() {
        return Arrays.asList(new Qualquer(1, "Eu"), new Qualquer(2, "Tu"), new Qualquer(3, "Ele"));
    }
}