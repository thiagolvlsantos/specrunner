package example;

import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.util.string.IStringProvider;

public class JQueryProvider implements IStringProvider {

    @Override
    public String newString(IContext context) throws ContextException {
        // return new
        // File("src/test/resources/income/jquery/").toURI().toString().replace("file:/",
        // "file://");
        return "http://localhost:8080/";
    }
}
