package org.specrunner.sql.report;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.util.xom.IPresentation;

/**
 * The register types.
 * 
 * @author Thiago Santos
 * 
 */
public enum RegisterType implements IPresentation {
    /**
     * Indicates a missing register.
     */
    MISSING("missing"),
    /**
     * Indicates an unexpected register.
     */
    ALIEN("alien"),
    /**
     * Registers match, but types there are some different columns.
     */
    DIFFERENT("different");

    /**
     * Type alias.
     */
    private String code;

    /**
     * Constructor.
     * 
     * @param code
     *            The text code.
     */
    private RegisterType(String code) {
        this.code = code;
    }

    /**
     * Gets the style of this type.
     * 
     * @return The style.
     */
    public String getStyle() {
        return "sr_" + code.toLowerCase();
    }

    @Override
    public String asString() {
        return code;
    }

    @Override
    public Node asNode() {
        return new Text(asString());
    }
}
