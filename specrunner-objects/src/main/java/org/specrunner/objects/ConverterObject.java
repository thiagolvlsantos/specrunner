package org.specrunner.objects;

import org.specrunner.plugins.PluginException;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

/**
 * Convert a reference to an object in mapped resources.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterObject implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public boolean accept(Object value) {
        return true;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (args == null || args.length == 0) {
            throw new ConverterException("Missing object type.");
        }
        if (!(args[0] instanceof Class)) {
            throw new ConverterException("Converter argument must be a class type.");
        }
        Class<?> type = (Class<?>) args[0];
        try {
            return PluginObjectManager.get().lookup(type, String.valueOf(value));
        } catch (PluginException e) {
            throw new ConverterException(e);
        }
    }
}