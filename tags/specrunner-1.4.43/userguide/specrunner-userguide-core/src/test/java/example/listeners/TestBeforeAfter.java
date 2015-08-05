package example.listeners;

import nu.xom.Node;

import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.junit.SRRunnerScenario;
import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;

import example.listeners.TestBeforeAfter.ListenerNull;

@RunWith(SRRunnerScenario.class)
@SRScenarioListeners(value = { ListenerNull.class, ListenerNull.class })
public class TestBeforeAfter {

    public static class ListenerNull implements IScenarioListener {

        private Object object;

        @Override
        public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
            object = new Object();
            System.out.println("BEFORE:" + object.toString());
        }

        @Override
        public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
            System.out.println("AFTER:" + object.toString());
        }
    }
}
