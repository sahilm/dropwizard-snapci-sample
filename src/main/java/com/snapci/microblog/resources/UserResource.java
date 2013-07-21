package com.snapci.microblog.resources;

import com.snapci.microblog.core.ErrorResponse;
import com.snapci.microblog.core.User;
import com.snapci.microblog.jdbi.UserDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static javax.ws.rs.core.Response.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO dao;

    public UserResource(UserDAO dao) {
        this.dao = dao;
    }

    @POST
    public Response create(@FormParam("name") String name) {
        User user = new User(name);
        try {
            dao.create(user);
        } catch (Exception e) {
            return ErrorResponse.fromException(e).build();
        }
        URI location = UriBuilder.fromPath(user.getName().toLowerCase()).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{name}")
    public Response show(@PathParam("name") String name) {
        User user = dao.findByName(name);
        if (user == null) {
            return new ErrorResponse(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(user).build();
    }

}
