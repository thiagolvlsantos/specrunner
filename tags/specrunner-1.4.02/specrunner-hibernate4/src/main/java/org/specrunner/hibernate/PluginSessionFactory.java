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

import java.lang.reflect.Method;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.context.IDestructable;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
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
public class PluginSessionFactory extends AbstractPluginFactory {

    /**
     * Default session factory name.
     */
    public static final String SESSION_FACTORY = "sessionFactory";

    /**
     * Feature to set configuration.
     */
    public static final String FEATURE_CONFIGURATION = PluginSessionFactory.class.getName() + ".configuration";
    /**
     * The configuration name.
     */
    private String configuration;

    /**
     * The session factory provider name.
     */
    public static final String FEATURE_TYPE = PluginSessionFactory.class.getName() + ".type";
    /**
     * Feature to set session factory factory.
     */
    public static final String FEATURE_FACTORY = PluginSessionFactory.class.getName() + ".factory";
    /**
     * Set feature factory method name.
     */
    public static final String FEATURE_METHOD = PluginSessionFactory.class.getName() + ".method";

    /**
     * Gets the configuration.
     * 
     * @return The configuration.
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * Sets the configuration.
     * 
     * @param configuration
     *            The configuration.
     */
    @DontEval
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (configuration == null) {
            fm.set(FEATURE_CONFIGURATION, this);
        }
        if (type == null) {
            fm.set(FEATURE_TYPE, this);
        }
        if (factory == null) {
            fm.set(FEATURE_FACTORY, this);
        }
        if (method == null) {
            fm.set(FEATURE_METHOD, this);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (configuration == null && type == null && (factory == null || method == null)) {
            throw new PluginException("Parameter 'configuration', 'type', or 'factory' and 'method' missing. In 'type' use an subtype of ISessionFactoryProvider, or choose a class in 'factory' whose 'method' is static and returns a Hibernate Configuration, or a 'configuration' by its name.");
        }
        try {
            final SessionFactory sf;
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

                ServiceRegistry serviceRegistry = createServiceRegistry(cfg);

                sf = cfg.buildSessionFactory(serviceRegistry);

                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("SessionFactory : " + sf + ".");
                }

                setListeners(sf);
            }

            String str = getName() != null ? getName() : SESSION_FACTORY;
            context.saveGlobal(str, new IDestructable() {
                @Override
                public Object getObject() {
                    return sf;
                }

                @Override
                public void destroy() {
                    sf.close();
                }
            });
            result.addResult(Success.INSTANCE, context.peek());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return ENext.SKIP;
    }

    /**
     * Create session service registry.
     * 
     * @param cfg
     *            A configuration.
     * @return A service registry.
     */
    protected ServiceRegistry createServiceRegistry(Configuration cfg) {
        Properties properties = cfg.getProperties();
        Environment.verifyProperties(properties);
        ConfigurationHelper.resolvePlaceHolders(properties);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(properties).build();
        return serviceRegistry;
    }

    /**
     * Add listeners to a session factory.
     * 
     * @param sessionFactory
     *            A session factory.
     */
    protected void setListeners(SessionFactory sessionFactory) {
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        HibernateListener listener = new HibernateListener(SRServices.get(IObjectManager.class).getEntities());
        registry.appendListeners(EventType.PRE_INSERT, listener);
        registry.appendListeners(EventType.POST_INSERT, listener);
    }

    /**
     * Get the session factory associated to a given name in test context.
     * 
     * @param context
     *            The context.
     * @param name
     *            The name.
     * @return The session factory instance.
     * @throws PluginException
     *             On session factory lookup errors.
     */
    public static SessionFactory getSessionFactory(IContext context, String name) throws PluginException {
        String str = name != null ? name : SESSION_FACTORY;
        SessionFactory sf = (SessionFactory) context.getByName(str);
        if (sf == null) {
            throw new PluginException("Instance of '" + str + "' not found. Use " + PluginSessionFactory.class.getName() + " before.");
        }
        return sf;
    }
}
