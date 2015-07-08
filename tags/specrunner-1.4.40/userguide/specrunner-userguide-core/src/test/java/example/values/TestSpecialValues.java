package example.values;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.converters.Converter;
import org.specrunner.junit.SRRunnerScenario;

@RunWith(SRRunnerScenario.class)
public class TestSpecialValues {

    @Before
    public void map() {
        SRServices.getExpressionFactory().bindClass("local", LocalDate.class);
    }

    public boolean isEmpty(String empty) {
        return "".equals(empty);
    }

    public boolean isNull(String empty) {
        return empty == null;
    }

    public boolean isDate(@Converter(name = "ld") LocalDate date) {
        System.out.println(date);
        return date != null;
    }

    public boolean isRawDate(LocalDate date) {
        System.out.println(date);
        return date != null;
    }
}