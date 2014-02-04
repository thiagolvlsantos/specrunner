package org.specrunner.application.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.specrunner.application.entities.Person;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PersonDao extends GenericDao<Person, PersonQuery> {

    public PersonDao() {
        super(Person.class);
    }

    @Override
    protected void appendToCriteria(PersonQuery query, Criteria criteria) {
        if (query.getFirstName() != null) {
            criteria.add(Restrictions.ilike("naming.first", query.getFirstName(), MatchMode.ANYWHERE));
        }
        if (query.getLastName() != null) {
            criteria.add(Restrictions.ilike("naming.last", query.getLastName(), MatchMode.ANYWHERE));
        }
    }
}
