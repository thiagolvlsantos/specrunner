package example.employee;

import org.specrunner.converters.impl.ConverterBooleanTemplate;

@SuppressWarnings("serial")
public class ConverterYesNo extends ConverterBooleanTemplate {

    public ConverterYesNo() {
        super("Sim", "Não");
    }
}