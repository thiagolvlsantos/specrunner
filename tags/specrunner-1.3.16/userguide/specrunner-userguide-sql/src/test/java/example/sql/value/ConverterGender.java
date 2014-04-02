package example.sql.value;

import org.specrunner.converters.core.ConverterEnumValueTemplate;

public class ConverterGender extends ConverterEnumValueTemplate {

    public ConverterGender() {
        super(Gender.class, "getCode", "getDescription");
    }
}
