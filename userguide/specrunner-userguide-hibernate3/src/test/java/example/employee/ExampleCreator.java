package example.employee;

import org.specrunner.context.IContext;
import org.specrunner.plugins.core.objects.IObjectCreator;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

public class ExampleCreator implements IObjectCreator {

    @Override
    public Object create(IContext context, TableAdapter table, RowAdapter row, Class<?> type) throws Exception {
        if (type == Contact.class) {
            Contact c = new Contact();
            c.setEmail("any@gmail.com");
            System.out.println("On Contact creation set mail:" + c.getEmail());
            return c;
        }
        return null;
    }

}
