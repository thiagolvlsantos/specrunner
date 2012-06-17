package org.specrunner.application.web.pages.panels.list;

import javax.swing.tree.TreeModel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.PropertyModel;

@SuppressWarnings("serial")
public class LinkTreeRegular extends LinkTree {
    private String propriedade;
    private String nomeLinks;

    public LinkTreeRegular(String id, String propriedade, String nomeLinks, TreeModel model) {
        super(id, model);
        this.propriedade = propriedade;
        this.nomeLinks = nomeLinks;
    }

    @Override
    protected Component newJunctionLink(MarkupContainer parent, final String id, final Object node) {
        Component c = super.newJunctionLink(parent, id, node);
        c.setMarkupId(nomeLinks + "_" + new PropertyModel<String>(node, propriedade).getObject());
        return c;
    }
}
