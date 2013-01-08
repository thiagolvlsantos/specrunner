package th.example;

import java.util.Date;

import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

public class ConverterDateText implements IConverter {
    @Override
    public void initialize() {
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        return String.valueOf(value).contains("atual") | String.valueOf(value).contains("actual") ? new Date() : null;
    }
}
