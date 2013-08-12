package org.specrunner.application.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.specrunner.application.entities.Parameter;
import org.specrunner.application.entities.ParameterType;

@Path("/parameter")
@Consumes("application/json")
@Produces("application/json")
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
    public Collection<Parameter> all() {
        return params.values();
    }

    @GET
    @Path("/select/{id}")
    public Response select(@PathParam("id") Long id) {
        Parameter tmp = params.get(id);
        if (tmp == null) {
            return Response.status(Status.NOT_FOUND).entity("Not found.").build();
        }
        return Response.ok(tmp).build();
    }

    @POST
    @Path("/insert")
    public Response insert(Parameter parameter) {
        Set<Long> keys = params.keySet();
        Long max = keys.isEmpty() ? 0 : Collections.max(keys);
        parameter.setId(max + 1);
        params.put(parameter.getId(), parameter);
        return Response.ok(parameter).status(Status.CREATED).build();
    }

    @PUT
    @Path("/update")
    public Response update(Parameter parameter) {
        Parameter tmp = params.get(parameter.getId());
        try {
            BeanUtils.copyProperties(tmp, parameter);
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        return Response.ok(tmp).build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        Parameter old = params.get(id);
        if (old == null) {
            return Response.status(Status.NOT_FOUND).entity("Not removed.").build();
        }
        params.remove(id);
        return Response.ok(old).build();
    }
}