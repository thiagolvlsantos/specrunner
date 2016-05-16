/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.hibernate4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.node.RowAdapter;

/**
 * Allow create object instances an make them persistent using Hibernate by
 * using save.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginInsert extends PluginHibernate {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result, SessionFactory sf) throws Exception {
        Session s = null;
        try {
            s = sf.openSession();
            s.save(instance);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
