package th.example;

import org.specrunner.junit.ExpectedMessages;
import org.specrunner.plugins.impl.language.Converter;

public class ObjetoAlvo {

    public void abrir(@Converter(name = "object", resultType = Person.class) Person p) {
        System.out.println("PESSOA:" + p);
    }

    @ExpectedMessages(messages = { "Converter named 'testing' not found." })
    public void erro(@Converter(name = "testing", resultType = Person.class) Person p) {
        System.out.println("PESSOA:" + p);
    }
}
