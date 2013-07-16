/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
import java.util.Iterator;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.mapping.PersistentClass;
import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.objects.PluginObjectManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;

/**
 * Creates/recovers a configuration instance and add it to the global context.
 * It can be done by using two strategies:
 * <ul>
 * <li>by setting 'type' attribute to a class that implements
 * <code>IConfigurationProvider</code>, i.e.
 * <code>type='myclass.MyConfigurationProvider'</code>;</li>
 * <li>or by setting 'factory' to any type and 'method' to a parameterless
 * static method which returns a <code>Configuration</code>, i.e.
 * <code>factory="myclass.Any" method="getConfig"</code>.
 * </ul>
 * 
 * To make configuration thread safe for in memory databases like Hypersonic the
 * URL connection can be changed for example to
 * 'jdbc:hsqldb:mem:phonebook<thread name>'. To allow this behavior set
 * threadsafe='true', or set the feature FEATURE_THREADSAFE to 'true'.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginConfiguration extends AbstractPluginFactory {

    /**
     * Default configuration name.
     */
    public static final String SESSION_CONFIGURATION = "sessionConfiguration";

    /**
     * Feature to set configuration provider class name.
     */
    public static final String FEATURE_TYPE = PluginConfiguration.class.getName() + ".type";
    /**
     * Feature to set configuration factory class.
     */
    public static final String FEATURE_FACTORY = PluginConfiguration.class.getName() + ".factory";
    /**
     * Feature to set method factory name.
     */
    public static final String FEATURE_METHOD = PluginConfiguration.class.getName() + ".method";

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
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
        if (type == null && (factory == null || method == null)) {
            throw new PluginException("Parameter 'type', or 'factory' and 'method' missing. In 'type' use an subtype of IConfigurationProvider, or choose a class in 'factory' whose 'method' is static and returns a Hibernate Configuration.");
        }
        try {
            Configuration cfg = null;
            if (type != null) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Configuration by type: " + type + ".");
                }
                Class<?> typeInstance = Class.forName(type);
                IConfigurationProvider conf = (IConfigurationProvider) typeInstance.newInstance();
                cfg = conf.getConfiguration();
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Configuration by factory/method: " + factory + "." + method + "()");
                }
                Class<?> typeInstance = Class.forName(factory);
                Method m = typeInstance.getMethod(method);
                cfg = (Configuration) m.invoke(null);
            }
            setListeners(cfg);

            if (getThreadsafe()) {
                changeUrl(context, cfg);
            }

            String str = getName() != null ? getName() : SESSION_CONFIGURATION;
            context.saveGlobal(str, cfg);
            result.addResult(Success.INSTANCE, context.peek());

            PluginObjectManager.get().clear();
            for (Iterator<?> ite = cfg.getClassMappings(); ite.hasNext();) {
                PersistentClass persistent = (PersistentClass) ite.next();
                PluginInsert pin = new PluginInsert();
                pin.setTypeInstance(Class.forName(persistent.getClassName()));
                PluginObjectManager.get().bind(pin);
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return ENext.SKIP;
    }

    /**
     * Set listeners of plugin.
     * 
     * @param cfg
     *            The configuration.
     */
    public void setListeners(Configuration cfg) {
        HibernateListener listener = new HibernateListener(PluginObjectManager.get().getEntities());
        PreInsertEventListener[] oldIn = cfg.getEventListeners().getPreInsertEventListeners();
        PreInsertEventListener[] in = null;
        if (oldIn != null) {
            in = new PreInsertEventListener[oldIn.length + 1];
            System.arraycopy(oldIn, 0, in, 0, oldIn.length);
            in[in.length - 1] = listener;
        } else {
            in = new PreInsertEventListener[] { listener };
        }
        cfg.getEventListeners().setPreInsertEventListeners(in);

        PostInsertEventListener[] oldOut = cfg.getEventListeners().getPostInsertEventListeners();
        PostInsertEventListener[] out = null;
        if (oldOut != null) {
            out = new PostInsertEventListener[oldOut.length + 1];
            System.arraycopy(oldOut, 0, out, 0, oldOut.length);
            out[out.length - 1] = listener;
        } else {
            out = new PostInsertEventListener[] { listener };
        }
        cfg.getEventListeners().setPostInsertEventListeners(out);
    }

    /**
     * Change URL if thread safe is true.
     * 
     * @param context
     *            The context.
     * @param cfg
     *            The configuration.
     */
    protected void changeUrl(IContext context, Configuration cfg) {
        String url = String.valueOf(SpecRunnerServices.get(IConcurrentMapping.class).get("url", cfg.getProperty(Environment.URL)));
        cfg.setProperty(Environment.URL, url);
        Node node = context.getNode();
        if (node instanceof Element) {
            Element ele = (Element) node;
            Attribute att = ele.getAttribute("title");
            if (att == null) {
                att = new Attribute("title", "");
                ele.addAttribute(att);
            }
            att.setValue("URL='" + url + "'");
        }
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Connection URL set to '" + url + "'.");
        }
    }

    /**
     * Gets the configuration object bound to the environment with a given name.
     * 
     * @param context
     *            The context.
     * @param name
     *            The presumed name of configuration.
     * @return The configuration with the given name, if exists, otherwise the
     *         configuration with default name, if exists, or null.
     * @throws PluginException
     *             On configuration lookup error.
     */
    public static Configuration getConfiguration(IContext context, String name) throws PluginException {
        String str = name != null ? name : SESSION_CONFIGURATION;
        Configuration cfg = (Configuration) context.getByName(str);
        if (cfg == null) {
            throw new PluginException("Instance of '" + str + "' not found. Use " + PluginConfiguration.class.getName() + " before.");
        }
        return cfg;
    }
}