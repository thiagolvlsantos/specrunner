package th.example;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;

@SuppressWarnings("serial")
public class ConverterSalary implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public boolean accept(Object value) {
        return true;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        String res = String.valueOf(value);
        res = res.substring(res.indexOf('$') + 1);
        return Double.valueOf(res.replace(".", "").replace(",", "."));
    }
}
