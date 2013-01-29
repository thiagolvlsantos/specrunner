package th.example;

import java.util.Calendar;

import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

@SuppressWarnings("serial")
public class ConverterDateAny implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, (int) (System.currentTimeMillis() % 28) + 1);
        c.set(Calendar.MONTH, (int) (System.currentTimeMillis() % 12) + 1);
        return c.getTime();
    }

}
