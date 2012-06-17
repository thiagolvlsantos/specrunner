package org.specrunner.application.dao;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public abstract class GenericDao<T, Q extends Query> {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Class<?> persistentClass;
    protected Validator validator;

    public GenericDao(Class<?> persistentClass) {
        this.persistentClass = persistentClass;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
    }

    public void save(T obj) {
        getSession().save(obj);
    }

    public void update(T obj) {
        getSession().saveOrUpdate(obj);
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

    @SuppressWarnings("unchecked")
    public Iterator<T> find(Q query) {
        Criteria c = getSession().createCriteria(persistentClass);
        c.setFirstResult(query.getFirst());
        c.setMaxResults(query.getCount());
        if (query.getAscending() != null && query.getProperty() != null) {
            if (query.getAscending()) {
                c.addOrder(Order.asc(query.getProperty()));
            } else {
                c.addOrder(Order.desc(query.getProperty()));
            }
        }
        appendToCriteria(query, c);
        return c.list().iterator();
    }

    public Integer total(Q query) {
        Criteria c = getSession().createCriteria(persistentClass);
        appendToCriteria(query, c);
        c.setProjection(Projections.rowCount());
        return ((Number) c.uniqueResult()).intValue();
    }

    protected abstract void appendToCriteria(Q query, Criteria criteria);
}