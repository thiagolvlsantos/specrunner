package example.text;

import nu.xom.Node;

import org.specrunner.annotations.IScenarioListener;
import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;

public class ScenarioListenerPrint implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
        System.out.println("BEFORE: " + title + " ON " + instance);
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) {
        System.out.println("AFTER: " + title + " ON " + instance);
    }
}
