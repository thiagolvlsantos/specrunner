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
    protected <T> void wrapperMethod(Class<T> type, CtMethod m) throws NotFoundException, CannotCompileException {
        StringBuilder before = new StringBuilder();

        String name = m.getName();
        CtClass[] types = m.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            sb.append((i == 0 ? "" : ",") + "%s");
        }
        m.addLocalVariable("time", CtClass.longType);
        before.append("time = System.currentTimeMillis();");
        before.append("System.out.println(\"-> :" + name + "(\" + String.format(\"" + sb + "\",$args)+\")\");");
        m.insertBefore(before.toString());

        StringBuilder after = new StringBuilder();
        after.append("System.out.println(\"<- :" + name + "(" + m.getReturnType().getName() + "):\"+$_);");
        after.append("System.out.println(\"ms :" + name + " = \"+(System.currentTimeMillis()-time));");
        m.insertAfter(after.toString());
    }
}