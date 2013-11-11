package example.employee;

import org.specrunner.plugins.impl.objects.IObjectCreator;
import org.specrunner.util.xom.RowAdapter;

public class ExampleCreator implements IObjectCreator {

    @Override
    public Object create(Class<?> type, RowAdapter row) throws Exception {
        if (type == Contact.class) {
            Contact c = new Contact();
            c.setEmail("any@gmail.com");
            System.out.println("On Contact creation set mail:" + c.getEmail());
            return c;
        }
        return null;
    }

}
