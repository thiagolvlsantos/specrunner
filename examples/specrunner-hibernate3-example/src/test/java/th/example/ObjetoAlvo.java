package th.example;

import org.specrunner.junit.ExpectedMessage;
import org.specrunner.plugins.impl.language.Converter;

public class ObjetoAlvo {

    public void abrir(@Converter(name = "object", resultType = Person.class) Person p) {
        System.out.println("PESSOA:" + p);
    }

    @ExpectedMessage(message = "Converter named 'testing' not found.")
    public void erro(@Converter(name = "testing", resultType = Person.class) Person p) {
        System.out.println("PESSOA:" + p);
    }
}
