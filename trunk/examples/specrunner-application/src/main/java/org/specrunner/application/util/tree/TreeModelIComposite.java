package org.specrunner.application.util.tree;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class TreeModelIComposite implements TreeModel, Serializable {

    private IComposite<?, ?> root;
    private List<TreeModelListener> listeners = new LinkedList<TreeModelListener>();

    public TreeModelIComposite(IComposite<?, ?> root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        return ((IComposite<?, ?>) parent).getChildren().get(index);
    }

    public int getChildCount(Object parent) {
        return ((IComposite<?, ?>) parent).getChildren().size();
    }

    public boolean isLeaf(Object node) {
        return ((IComposite<?, ?>) node).isEmpty();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((IComposite<?, ?>) parent).getChildren().indexOf(child);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("valueForPathChanged" + path + "->" + newValue);
        for (TreeModelListener t : listeners) {
            t.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
}
