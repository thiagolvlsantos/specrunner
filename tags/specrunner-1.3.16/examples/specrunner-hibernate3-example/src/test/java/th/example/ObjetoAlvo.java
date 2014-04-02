package th.example;

import org.specrunner.converters.Converter;
import org.specrunner.junit.ExpectedMessage;

public class ObjetoAlvo {

    public void abrir(Person p) {
        System.out.println("PESSOA:" + p);
    }

    @ExpectedMessage("Converter named 'testing' not found.")
    public void erro(@Converter(name = "testing") Person p) {
        System.out.println("PESSOA:" + p);
    }
}
