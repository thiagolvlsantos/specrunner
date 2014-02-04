package org.specrunner.application.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.specrunner.application.entities.Unit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UnitDao extends GenericDao<Unit, UnitQuery> {

    public UnitDao() {
        super(Unit.class);
    }

    @Override
    protected void appendToCriteria(UnitQuery query, Criteria criteria) {
        if (query.getName() != null) {
            criteria.add(Restrictions.ilike("name", query.getName(), MatchMode.ANYWHERE));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Unit> getRoots() {
        Criteria c = getSession().createCriteria(Unit.class);
        c.add(Restrictions.isNull("parent"));
        return c.list();
    }
}
