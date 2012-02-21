package com.example;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.result.IResultSet;

public class CheckName extends AbstractPlugin {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        String esperado = context.getNode().getValue();
        String recebido = new Greeter().greetingFor(name);
        UtilPlugin.compare(context.getNode(), result, esperado, recebido);
    }

}