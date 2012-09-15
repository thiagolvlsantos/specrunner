package org.specrunner.sql.proxy.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.specrunner.sql.proxy.AbstractWrapperFactoryJdbc;

/**
 * Example of wrapper factory.
 * 
 * @author Thiago Santos
 * 
 */
public class WrapperFactoryExample extends AbstractWrapperFactoryJdbc {

    @Override
    protected void wrapperMethod(CtMethod m) throws NotFoundException, CannotCompileException {
        CtClass[] types = m.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            sb.append((i == 0 ? "" : ",") + "%s");
        }
        String name = m.getName();

        StringBuilder before = new StringBuilder();
        m.addLocalVariable("time", CtClass.longType);
        before.append("time = System.currentTimeMillis();");
        before.append("System.out.println(\"-> " + name + "(\" + String.format(\"" + sb + "\",$args)+\")\");");
        m.insertBefore(before.toString());

        StringBuilder after = new StringBuilder();
        after.append("System.out.println(\"<- " + name + "(" + m.getReturnType().getName() + "):\"+$_ +\",TIME:\"+(System.currentTimeMillis()-time) );");
        m.insertAfter(after.toString());
    }
}