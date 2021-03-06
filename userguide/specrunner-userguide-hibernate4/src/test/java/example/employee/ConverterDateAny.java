package example.employee;

import java.util.Calendar;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;

@SuppressWarnings("serial")
public class ConverterDateAny implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public boolean accept(Object value) {
        return true;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, (int) (System.currentTimeMillis() % 28) + 1);
        c.set(Calendar.MONTH, (int) (System.currentTimeMillis() % 12) + 1);
        return c.getTime();
    }

}
