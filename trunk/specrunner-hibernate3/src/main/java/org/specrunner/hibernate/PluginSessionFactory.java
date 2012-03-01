/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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

import java.lang.reflect.Method;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;

/**
 * Creates/recovers a SessionFactory instance and add it to the plugin global
 * context, it can be done using three strategies:
 * <ul>
 * <li>by setting 'configuration' attribute to a name used previously by a
 * PluginConfiguration;</li>
 * <li>by setting 'type' attribute to a class that implements
 * <code>IConfigurationProvider</code>, i.e.
 * <code>type='myclass.SessionConfigurationImpl'</code>;</li>
 * <li>or by setting 'factory' to any type and 'method' to a parameterless
 * static method which returns a <code>Configuration</code>, i.e.
 * <code>factory="myclass.Any" method="getConfig"</code>.
 * </ul>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSessionFactory extends AbstractPluginScoped {

    public static final String SESSION_FACTORY = "sessionFactory";

    public static final String FEATURE_CONFIGURATION = PluginConfiguration.class.getName() + ".configuration";
    private String configuration;

    public static final String FEATURE_TYPE = PluginConfiguration.class.getName() + ".type";
    private String type;

    public static final String FEATURE_FACTORY = PluginConfiguration.class.getName() + ".factory";
    private String factory;

    public static final String FEATURE_METHOD = PluginConfiguration.class.getName() + ".method";
    private String method;

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        if (configuration == null) {
            try {
                fh.set(FEATURE_CONFIGURATION, "configuration", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (type == null) {
            try {
                fh.set(FEATURE_TYPE, "type", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (factory == null) {
            try {
                fh.set(FEATURE_FACTORY, "factory", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (method == null) {
            try {
                fh.set(FEATURE_METHOD, "method", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        try {
            SessionFactory sf = null;
            if (type != null) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("SessionFactory by type: " + type + ".");
                }
                Class<?> typeInstance = Class.forName(type);
                ISessionFactoryProvider conf = (ISessionFactoryProvider) typeInstance.newInstance();
                sf = conf.getSessioFactory();
            } else if (factory != null && method != null) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("SessionFactory by factory/method: " + factory + "." + method + "()");
                }
                Class<?> typeInstance = Class.forName(factory);
                Method m = typeInstance.getMethod(method);
                sf = (SessionFactory) m.invoke(null);
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("SessionFactory by Configuration named: " + configuration + ".");
                }
                Configuration cfg = PluginConfiguration.getConfiguration(context, configuration);
                sf = cfg.buildSessionFactory();
            }

            String str = getName() != null ? getName() : SESSION_FACTORY;
            context.saveGlobal(str, sf);
            result.addResult(Status.SUCCESS, context.peek());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return ENext.SKIP;
    }

    public static SessionFactory getSessionFactory(IContext context, String name) throws PluginException {
        String str = name != null ? name : SESSION_FACTORY;
        SessionFactory sf = (SessionFactory) context.getByName(str);
        if (sf == null) {
            throw new PluginException("Instance of '" + str + "' not found. Use " + PluginSessionFactory.class.getName() + " before.");
        }
        return sf;
    }
}
