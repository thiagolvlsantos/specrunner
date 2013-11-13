package org.specrunner.application.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ParameterApplication extends Application {
    private static Set<Object> singletons = new HashSet<Object>();

    public ParameterApplication() {
        singletons.add(new ParameterService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
