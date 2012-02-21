package org.specrunner.application.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.specrunner.application.entities.Employee;
import org.specrunner.application.entities.Person;
import org.specrunner.application.entities.Unit;

public class DumpSchema {

    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(Unit.class);
        cfg.addAnnotatedClass(Person.class);
        cfg.addAnnotatedClass(Employee.class);
        cfg = cfg.configure("/hibernate.cfg.xml");
        new SchemaExport(cfg).create(true, false);
    }
}
