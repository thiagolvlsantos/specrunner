package th.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

@SuppressWarnings("serial")
public class ConverterDatePattern implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(String.valueOf(args[0])).parse(String.valueOf(value));
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}
