package org.specrunner.hibernate;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.specrunner.context.IContext;
import org.specrunner.objects.AbstractPluginObject;
import org.specrunner.objects.IObjectSelector;
import org.specrunner.result.IResultSet;
import org.specrunner.util.impl.RowAdapter;

/**
 * Hibernate object selector.
 * 
 * @author Thiago Santos
 * 
 */
public class ObjectSelector implements IObjectSelector<Session> {

    /**
     * Thread safe instance of <code>IFinder</code>.
     */
    private static ThreadLocal<ObjectSelector> instance = new ThreadLocal<ObjectSelector>() {
        @Override
        protected ObjectSelector initialValue() {
            return new ObjectSelector();
        };
    };

    /**
     * The object session factory.
     */
    protected Session session;

    /**
     * Gets the thread safe instance of finder.
     * 
     * @return The finder instance.
     */
    public static ObjectSelector get() {
        return instance.get();
    }

    @Override
    public Session getSource(AbstractPluginObject plugin, IContext context) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sf = PluginSessionFactory.getSessionFactory(context, plugin.getName());
            session = sf.openSession();
        }
        return session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> select(AbstractPluginObject plugin, IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        Criteria c = session.createCriteria(instance.getClass());
        String[] keyFields = plugin.getReference().split(",");
        for (int i = 0; i < keyFields.length; i++) {
            c.add(Restrictions.eq(keyFields[i], PropertyUtils.getProperty(instance, keyFields[i])));
        }
        return c.list();
    }

    @Override
    public void release() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}