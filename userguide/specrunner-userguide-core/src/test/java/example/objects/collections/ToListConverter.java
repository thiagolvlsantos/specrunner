package example.objects.collections;

import java.util.ArrayList;
import java.util.List;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterNotNullNotEmpty;

@SuppressWarnings("serial")
public class ToListConverter extends ConverterNotNullNotEmpty {

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        Object tmp = super.convert(obj, args);
        if (tmp == null) {
            return tmp;
        }
        if (args.length == 0) {
            args = new String[] { ";" };
        }
        String[] strs = String.valueOf(obj).split(String.valueOf(args[0]));
        List<String> result = new ArrayList<>(strs.length);
        for (int i = 0; i < strs.length; i++) {
            result.add(strs[i].trim());
        }
        return result;
    }
}
