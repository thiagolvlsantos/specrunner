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
package org.specrunner.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.specrunner.SRServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.core.AbstractReusable;
import org.specrunner.util.xom.node.RowAdapter;

public class PluginEntityManager extends AbstractPluginObject {

    private String unit;
    private String url;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected boolean isMapped() {
        return true;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {

        EntityManagerFactory emf = null;
        EntityManager em = null;

        IReuseManager rm = SRServices.get(IReuseManager.class);
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put("unit", unit);

        Map<String, Object> properties = new HashMap<String, Object>();
        if (getThreadsafe() && url != null) {
            String key = "javax.persistence.jdbc.url";
            IConcurrentMapping cm = SRServices.get(IConcurrentMapping.class);
            String newUrl = String.valueOf(cm.get("url", url));
            properties.put(key, newUrl);
        }

        String emfName = "entityManagerFactory" + unit;
        IReusable<?> irFact = rm.get(emfName);
        if (irFact != null && irFact.canReuse(cfg)) {
            emf = (EntityManagerFactory) irFact.getObject();
        } else {
            emf = Persistence.createEntityManagerFactory(unit, properties);
        }

        String emName = "entityManager" + unit;
        IReusable<?> irEm = rm.get(emName);
        if (irEm != null && irEm.canReuse(cfg)) {
            em = (EntityManager) irEm.getObject();
        } else {
            em = emf.createEntityManager();
        }

        final EntityManager e = em;
        rm.put(emName, new AbstractReusable<EntityManager>(emName, e) {
            @Override
            public void reset() {
                e.clear();
            }

            @Override
            public void release() {
                if (e.isOpen()) {
                    e.close();
                }
            }

            @Override
            public boolean canReuse(Map<String, Object> cfg) {
                return unit.equals(cfg.get("unit"));
            }
        });

        final EntityManagerFactory f = emf;
        rm.put(emfName, new AbstractReusable<EntityManagerFactory>(emfName, f) {
            @Override
            public void reset() {
            }

            @Override
            public void release() {
                if (f.isOpen()) {
                    f.close();
                }
            }

            @Override
            public boolean canReuse(Map<String, Object> cfg) {
                return unit.equals(cfg.get("unit"));
            }
        });

        EntityTransaction et = em.getTransaction();
        et.begin();
        em.merge(instance);
        et.commit();
    }
}