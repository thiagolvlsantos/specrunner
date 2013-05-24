package example.sql;

import java.util.Date;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;

public class OutroPlugin extends AbstractPlugin {

    private String texto;
    private String outro;

    public void salvar(Date date) {
        texto = date.toString();
    }

    private void outro(Date date) {
        outro = "outro " + date.toString();
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        result.addResult(Success.INSTANCE, context.peek(), "O texto é:" + texto);
    }
}