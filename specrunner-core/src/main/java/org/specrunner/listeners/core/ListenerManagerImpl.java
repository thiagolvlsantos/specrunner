/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.listeners.core;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.ISpecRunnerListener;

/**
 * Default listener manager.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ListenerManagerImpl extends LinkedList<ISpecRunnerListener> implements IListenerManager {

    @Override
    public void reset() {
        for (ISpecRunnerListener li : this) {
            li.reset();
        }
    }

    @Override
    public boolean add(ISpecRunnerListener e) {
        if (e == null) {
            return false;
        }
        // remove repetition
        remove(e.getName());
        // add element
        return super.add(e);
    }

    @Override
    public void remove(String name) {
        ISpecRunnerListener listener = null;
        for (ISpecRunnerListener li : this) {
            if (li.getName().equals(name)) {
                listener = li;
                break;
            }
        }
        if (listener != null) {
            remove(listener);
        }
    }

    @Override
    public <T extends ISpecRunnerListener> List<T> filterByType(Class<T> type) {
        List<T> result = new LinkedList<T>();
        for (ISpecRunnerListener lis : this) {
            if (type.isAssignableFrom(lis.getClass())) {
                result.add(type.cast(lis));
            }
        }
        return result;
    }
}
