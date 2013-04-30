package example.sql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSR {

    @Before
    public void antes() {
        System.out.println("antes");
        SpecRunnerServices.get(IExpressionFactory.class).bindClass("dt", Date.class);
    }

    public String call(Date date) {
        return "funciona " + date;
    }

    public String greeting(String nome) {
        return "Oi " + nome + "!";
    }

    public boolean bool(int index) {
        return index % 2 == 0;
    }

    public List<?> chamar(Date dt) {
        return Arrays.asList("a", "b", "c");
    }

    @After
    public void depois() {
        System.out.println("depois");
    }
}
