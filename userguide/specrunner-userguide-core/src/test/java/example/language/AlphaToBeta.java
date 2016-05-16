package example.language;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterNotNullNotEmpty;

@SuppressWarnings("serial")
public class AlphaToBeta extends ConverterNotNullNotEmpty {

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        String tmp = String.valueOf(obj);
        return tmp.equals("alpha") ? "beta" : tmp;
    }
}
