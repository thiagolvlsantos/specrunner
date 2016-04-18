package example.objects;

import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.converters.Converter;
import org.specrunner.converters.core.ConverterDefault;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.functions.IPredicate;

import example.objects.fields.User;

@RunWith(SRRunnerScenario.class)
public class TestField {

    public void callUser(User user) {
        System.out.println(user);
        SRServices.getObjectManager().clear();
    }

    public void getUser(@Converter(type = ConvGet.class) User user) {
        System.out.println(user);
    }

    public static class ConvGet extends ConverterDefault {
        public Object convert(final Object obj, Object[] args) throws org.specrunner.converters.ConverterException {
            try {
                return SRServices.getObjectManager().get(User.class, new IPredicate<User>() {
                    @Override
                    public Boolean apply(User p) {
                        return p.getContact().getInformation().equals(String.valueOf(obj));
                    }
                });
            } catch (PluginException e) {
                throw new RuntimeException(e);
            }
        }
    }
}