package org.specrunner.plugins.impl.language;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.IElementHolder;
import org.specrunner.util.xom.UtilNode;

public class PluginSentence extends AbstractPlugin {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        IElementHolder holder = UtilNode.newElementAdapter(context.getNode());

        /*
         * TODO: 1) FILTRA O TEXTO QUE VIRA MÉTODO; 2) EXTRAIR OS PARÂMETROS: A)
         * COMO STRING "<PARAMETRO>"; B) COMO <ARG>...</ARG> OU
         * <..CLASS="ARG">...</ARG>; C) MISTURANDO AMBOS.
         */

        System.out.println("CHAMAR:" + UtilString.camelCase(String.valueOf(holder.getObject(context, true))));
    }
}