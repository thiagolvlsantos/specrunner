package example.sql;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

public class MeuPlugin extends AbstractPlugin {

    public static final String FEATURE_PADRAO = MeuPlugin.class.getName() + ".padrao";
    private String padrao = "sim";

    public String ok;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
        fm.set(FEATURE_PADRAO, this);
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (padrao.equals(ok)) {
            result.addResult(Success.INSTANCE, context.peek());
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), "Parâmetro ok deve ser '" + padrao + "', recebido>'" + ok + "'");
        }
    }
}