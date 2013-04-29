package example.sql;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSR {

    @Before
    public void antes() {
        System.out.println("antes");
    }

    public String call(Date date) {
        return "funciona " + date;
    }

     public String greeting(String nome) {
         return "Oi " + nome + "!";
     }

    @After
    public void depois() {
        System.out.println("depois");
    }
}
