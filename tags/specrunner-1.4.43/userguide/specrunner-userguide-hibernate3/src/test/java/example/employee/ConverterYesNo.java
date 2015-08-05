package example.employee;

import org.specrunner.converters.core.ConverterBooleanTemplate;

@SuppressWarnings("serial")
public class ConverterYesNo extends ConverterBooleanTemplate {

    public ConverterYesNo() {
        super("Sim", "Não");
    }
}
