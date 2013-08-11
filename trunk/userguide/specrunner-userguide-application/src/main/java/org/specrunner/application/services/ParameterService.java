package org.specrunner.application.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.specrunner.application.entities.Parameter;
import org.specrunner.application.entities.ParameterType;

@Path("/parameter")
public class ParameterService {

    private static Map<Long, Parameter> params = new HashMap<Long, Parameter>();

    static {
        Parameter p1 = new Parameter(ParameterType.BOOLEAN, Boolean.TRUE);
        p1.setId(1L);

        Parameter p2 = new Parameter(ParameterType.DOUBLE, 10.5);
        p2.setId(2L);

        Parameter p3 = new Parameter(ParameterType.LONG, 35L);
        p3.setId(3L);

        Parameter p4 = new Parameter(ParameterType.STRING, "working!");
        p4.setId(4L);

        params.put(p1.getId(), p1);
        params.put(p2.getId(), p2);
        params.put(p3.getId(), p3);
        params.put(p4.getId(), p4);
    }

    @GET
    @Path("/all")
    @Produces("application/json")
    public Collection<Parameter> get() {
        return params.values();
    }

    @GET
    @Path("/get/{id}")
    @Produces("application/json")
    public Parameter get(@PathParam("id") Long id) {
        return params.get(id);
    }

    @POST
    @Path("/set/{id}")
    @Consumes("application/json")
    public Response set(@PathParam("id") Long id, Parameter parameter) {
        Parameter p = params.get(id);
        try {
            BeanUtils.copyProperties(p, parameter);
        } catch (IllegalAccessException e) {
            return Response.status(Status.BAD_REQUEST).build();
        } catch (InvocationTargetException e) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        return Response.ok(p).build();
    }
}