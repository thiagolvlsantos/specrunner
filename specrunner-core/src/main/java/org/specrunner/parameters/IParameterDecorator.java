/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.parameters;

import java.util.Map;

import org.specrunner.context.IContext;

/**
 * Stand for anything that can have parameters set. It is a bypass parameters
 * capturer.
 * 
 * @author Thiago Santos
 * 
 */
public interface IParameterDecorator {

    /**
     * All expressions are evaluated unless annotation <code>@DontEval</code> is
     * added. If the attribute has this flag the evaluation behavior is the
     * opposite: not annotated attributes wont be evaluated, and annotate
     * attributes will be evaluated.
     */
    String INVERT_FLAG = "_";

    /**
     * Invert evaluation 'silence'. All expression are silent unless
     * <code>@Unsilent</code> is specified for the feature.
     */
    String SILENT_FLAG = "-";

    /**
     * Force the evaluation at the end of plugin execution. Attributes will be
     * evaluated on end.
     */
    String LATE_FLAG = ".";

    /**
     * Gets the decorated object.
     * 
     * @return The object.
     */
    Object getDecorated();

    /**
     * Sets the decorated object. Reset all parameters information.
     * 
     * @param decorated
     *            The new decorated object.
     */
    void setDecorated(Object decorated);

    /**
     * Check if a given name has to be evaluated.
     * 
     * @param name
     *            The feature name.
     * @return true, to evaluate, false, otherwise. Default is true.
     */
    boolean isEval(String name);

    /**
     * Check if a given name has to be evaluated silently or not.
     * 
     * @param name
     *            The feature name.
     * @return true, to silent evaluation, false, otherwise. Default is true.
     */
    boolean isSilent(String name);

    /**
     * Check if a given name has to be evaluated at the end or not.
     * 
     * @param name
     *            The feature name.
     * @return true, to late evaluation, false, otherwise. Default is false.
     */
    boolean isLate(String name);

    /**
     * Get name cleared.
     * 
     * @param name
     *            The name.
     * @return Cleared string.
     */
    String clear(String name);

    /**
     * Set a parameter.
     * 
     * @param name
     *            The parameter name.
     * @param value
     *            The parameter value.
     * @param context
     *            The context where value is insert.
     * @return The resulting value of setting 'name' with the value in a given
     *         context. i.e. If context has a variable 'x' bind to 1(integer)
     *         and we ask to set 'name' to 'x', the operation output will be
     *         '1', but if feature 'name' in the decorated object has a
     *         annotation @DontEval the result will be 'x'.
     * @throws Exception
     *             On evaluation error.
     */
    Object setParameter(String name, Object value, IContext context) throws Exception;

    /**
     * Gets a parameter.
     * 
     * @param name
     *            The parameter name.
     * @return The parameter value.
     */
    Object getParameter(String name);

    /**
     * Check if a parameter name is valid.
     * 
     * @param name
     *            The parameter.
     * @return The parameter name.
     */
    boolean hasParameter(String name);

    /**
     * Map of parameters set. Only parameters properly set to object are
     * available.
     * 
     * @return The parameter mapping containing only parameters properly set.
     */
    Map<String, Object> getParameters();

    /**
     * Set parameters map.
     * 
     * @param parameters
     *            The map.
     */
    void setParameters(Map<String, Object> parameters);

    /**
     * Map of parameters set. All parameters tried to be set in the object, even
     * those whose corresponding property is not present in object.
     * 
     * @return The parameter mapping containing all parameters set.
     */
    Map<String, Object> getAllParameters();

    /**
     * Set of all parameters map.
     * 
     * @param allParameters
     *            The map.
     */
    void setAllParameters(Map<String, Object> allParameters);

}