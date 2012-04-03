package th.example;

import org.specrunner.objects.IObjectCreator;
import org.specrunner.util.impl.RowAdapter;

public class CriadorExemplo implements IObjectCreator {

    @Override
    public Object create(Class<?> type, RowAdapter row) throws Exception {
        if (type == Contact.class) {
            Contact c = new Contact();
            c.setEmail("qualquer@gmail.com");
            System.out.println("NOVO INNER:" + c);
            return c;
        }
        return null;
    }

}
