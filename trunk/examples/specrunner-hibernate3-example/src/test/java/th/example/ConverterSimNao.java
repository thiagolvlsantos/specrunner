package th.example;

import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

public class ConverterSimNao implements IConverter {
    @Override
    public void initialize() {
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        String valor = String.valueOf(value);
        if ("Sim".equalsIgnoreCase(valor)) {
            return Boolean.TRUE;
        } else if ("Não".equalsIgnoreCase(valor)) {
            return Boolean.FALSE;
        } else {
            throw new ConverterException("Valor '" + valor + "' inválido, usar \n'Sim' ou 'Não'");
        }
    }
}
