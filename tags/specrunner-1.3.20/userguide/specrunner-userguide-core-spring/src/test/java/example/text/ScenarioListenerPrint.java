package example.text;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;

public class ScenarioListenerPrint implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result) {
        System.out.println("BEFORE: " + title);
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result) {
        System.out.println("AFTER: " + title);
    }
}