package org.specrunner.plugins;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.IPresentation;

/**
 * A type for plugins.
 * 
 * @author Thiago Santos
 * 
 */
public class ActionType implements Comparable<ActionType>, IPresentation {

    /**
     * The type name.
     */
    protected String name;
    /**
     * Its priority, for example, ASSERTION has higher priority than ACTION,
     * which has higher priority than UNDEF.
     */
    protected double importance;

    /**
     * Creates a type.
     * 
     * @param name
     *            The name.
     * @param importance
     *            The importance.
     */
    protected ActionType(String name, double importance) {
        this.name = name;
        this.importance = importance;
    }

    /**
     * The type name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * The CSS which represents the type.
     * 
     * @return The CSS name.
     */
    public String getCssName() {
        return "sr_" + getName();
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionType other = (ActionType) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ActionType o) {
        if (importance - o.importance > 0) {
            return -1;
        } else if (importance - o.importance < 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public Node asNode() {
        Element result = new Element("span");
        result.addAttribute(new Attribute("class", getCssName()));
        result.appendChild(getName());
        return result;
    }
}