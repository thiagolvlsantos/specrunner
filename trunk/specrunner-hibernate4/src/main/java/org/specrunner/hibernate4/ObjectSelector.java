package org.specrunner.hibernate4;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.specrunner.context.IContext;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.core.objects.IObjectSelector;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.RowAdapter;

/**
 * Hibernate object selector.
 * 
 * @author Thiago Santos
 * 
 */
public class ObjectSelector implements IObjectSelector<Session> {

    /**
     * Thread safe instance of <code>ObjectSelector</code>.
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
    public Session getSource(AbstractPluginObject caller, IContext context) throws Exception {
        // recover the plugin session factory.
        if (session == null) {
            SessionFactory sf = PluginSessionFactory.getSessionFactory(context, caller.getName());
            session = sf.openSession();
        }
        return session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> all(AbstractPluginObject caller, IContext context, IResultSet result) throws Exception {
        session = getSource(caller, context);
        return session.createCriteria(instance.getClass()).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> select(AbstractPluginObject caller, IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        session = getSource(caller, context);
        Criteria c = session.createCriteria(instance.getClass());
        String[] keyFields = caller.getReference().split(",");
        for (int i = 0; i < keyFields.length; i++) {
            c.add(Restrictions.eq(keyFields[i], PropertyUtils.getProperty(instance, keyFields[i])));
        }
        return c.list();
    }

    @Override
    public void release() throws Exception {
        // close every time to allow reuse of object in different tests
        if (session != null) {
            session.close();
            session = null;
        }
    }
}