package example.listeners;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.junit.SRRunner;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;

@RunWith(SRRunner.class)
public class TestListeners {

    public static class PluginSourceListener implements ISourceListener {
        private String name;
        private String gap = "";

        private PluginSourceListener(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void reset() {
            System.out.println("reset(" + name + ")");
        }

        @Override
        public void onBefore(ISource source, IContext context, IResultSet result) {
            gap += "\t";
            System.out.println(gap + "onBeforeSource(" + name + ")");
        }

        @Override
        public void onAfter(ISource source, IContext context, IResultSet result) {
            System.out.println(gap + "onAfterSource(" + name + ")");
            gap = gap.substring(gap.length() - 1);
        }

    }

    public static class PluginPrintListener implements IPluginListener {
        private String name;
        private String gap = "";

        private PluginPrintListener(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void reset() {
            System.out.println("reset(" + name + ")");
        }

        @Override
        public void onBeforeInit(IPlugin plugin, IContext context, IResultSet result) {
            gap += "\t";
            System.out.println(gap + "onBeforeInit(" + name + ")");
        }

        @Override
        public void onAfterInit(IPlugin plugin, IContext context, IResultSet result) {
            System.out.println(gap + "onAfterInit(" + name + ")");
            gap = gap.substring(gap.length() - 1);
        }

        @Override
        public void onBeforeStart(IPlugin plugin, IContext context, IResultSet result) {
            gap += "\t";
            System.out.println(gap + "onBeforeStart(" + name + ")");
        }

        @Override
        public void onAfterStart(IPlugin plugin, IContext context, IResultSet result) {
            System.out.println(gap + "onAfterStart(" + name + ")");
            gap = gap.substring(gap.length() - 1);
        }

        @Override
        public void onBeforeEnd(IPlugin plugin, IContext context, IResultSet result) {
            gap += "\t";
            System.out.println(gap + "onBeforeEnd(" + name + ")");
        }

        @Override
        public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
            System.out.println(gap + "onAfterEnd(" + name + ")");
            gap = gap.substring(gap.length() - 1);
        }
    }

    @Before
    public void configure() {
        IListenerManager manager = SRServices.get(IListenerManager.class);

        manager.add(new PluginSourceListener("1"));
        manager.add(new PluginSourceListener("2"));
        manager.add(new PluginPrintListener("A"));
        manager.add(new PluginPrintListener("B"));
    }

    @After
    public void end() {
        IListenerManager manager = SRServices.get(IListenerManager.class);
        manager.remove("1");
        manager.remove("2");
        manager.remove("A");
        manager.remove("B");
    }
}