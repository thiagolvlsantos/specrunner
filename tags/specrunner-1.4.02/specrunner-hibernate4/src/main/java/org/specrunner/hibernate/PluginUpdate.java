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
package org.specrunner.hibernate;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.core.objects.AbstractPluginObjectSelectUnique;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.util.xom.RowAdapter;

/**
 * Update object in database.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginUpdate extends AbstractPluginObjectSelectUnique<Session> {

    /**
     * Create an update plugin.
     */
    public PluginUpdate() {
        super(ObjectSelector.get());
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void perform(IContext context, Object base, Object instance, RowAdapter row, IResultSet result) throws Exception {
        try {
            for (Field f : fields) {
                if (!f.isReference()) {
                    BeanUtils.copyProperty(base, f.getFullName(), instance);
                }
            }
            source.update(base);
            source.flush();
            for (int i = 0; i < row.getCellsCount(); i++) {
                result.addResult(Success.INSTANCE, context.newBlock(row.getCell(i).getNode(), this));
            }
        } catch (Exception e) {
            for (int i = 0; i < row.getCellsCount(); i++) {
                result.addResult(i == 0 ? Failure.INSTANCE : Warning.INSTANCE, context.newBlock(row.getCell(i).getNode(), this), i == 0 ? e : null);
            }
        }
    }
}