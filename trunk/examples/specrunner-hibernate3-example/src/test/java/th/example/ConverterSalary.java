package th.example;

import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

public class ConverterSalary implements IConverter {
    @Override
    public void initialize() {
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        String res = String.valueOf(value);
        res = res.substring(res.indexOf('$') + 1);
        return Double.valueOf(res.replace(".", "").replace(",", "."));
    }
}
