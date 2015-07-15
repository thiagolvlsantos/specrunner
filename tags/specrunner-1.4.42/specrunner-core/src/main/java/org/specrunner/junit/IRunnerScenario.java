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
package org.specrunner.junit;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.specrunner.listeners.INodeListener;

/**
 * SpecRunner executor.
 * 
 * @author Thiago Santos
 * 
 */
public interface IRunnerScenario {

    /**
     * Get test class.
     * 
     * @return A class.
     */
    TestClass getTestClass();

    /**
     * The notifier.
     * 
     * @return A notifier.
     */
    RunNotifier getNotifier();

    /**
     * The fake method.
     * 
     * @return A fake method.
     */
    FrameworkMethod getFakeMethod();

    /**
     * Set fake method.
     * 
     * @param method
     *            A method.
     */
    void setFakeMethod(FrameworkMethod method);

    /**
     * Get description.
     * 
     * @param method
     *            A method.
     * @return Description.
     */
    Description describeChild(FrameworkMethod method);

    /**
     * Scenario listeners.
     * 
     * @return A scenario listeners set.
     */
    List<INodeListener> getListeners();

    /**
     * Get listeners.
     * 
     * @param listeners
     *            A set of listeners.
     */
    void setListeners(List<INodeListener> listeners);

    /**
     * The fixture object, if it exists, null, otherwise.
     * 
     * @return Instance object.
     */
    Object getInstance();

    /**
     * Statement performed.
     * 
     * @return A statement.
     */
    SpecRunnerStatement getStatement();
}