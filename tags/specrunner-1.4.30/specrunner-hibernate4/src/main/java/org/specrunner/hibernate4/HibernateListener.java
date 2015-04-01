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
package org.specrunner.hibernate4;

import java.util.Map;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.util.UtilLog;

/**
 * A Hibernate listener to monitor object insertions.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class HibernateListener implements PreInsertEventListener, PostInsertEventListener {

    /**
     * Map of objects inserted.
     */
    protected Map<Class<?>, AbstractPluginObject> entities;
    /**
     * Key of object before inclusion.
     */
    protected String keyBefore;

    /**
     * Creates a listener with a entities mapping.
     * 
     * @param entities
     *            The entity mapping by type.
     */
    public HibernateListener(Map<Class<?>, AbstractPluginObject> entities) {
        this.entities = entities;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        AbstractPluginObject pin = entities.get(event.getEntity().getClass());
        try {
            keyBefore = pin.makeKey(event.getEntity());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        AbstractPluginObject pin = entities.get(event.getEntity().getClass());
        try {
            String keyAfter = pin.makeKey(event.getEntity());
            pin.mapObject(event.getEntity(), keyBefore, keyAfter);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("HibernateListener (before=" + keyBefore + ",after=" + keyAfter + "), bound to " + event.getEntity() + ".");
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
