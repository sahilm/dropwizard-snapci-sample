package com.snapci.microblog.resources;


import com.snapci.microblog.core.ErrorResponse;
import com.snapci.microblog.core.MicroBlog;
import com.snapci.microblog.core.User;
import com.snapci.microblog.jdbi.MicroBlogDAO;
import com.snapci.microblog.jdbi.UserDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

@Path("/{user}/blog")
@Produces(MediaType.APPLICATION_JSON)
public class MicroBlogResource {
    private final MicroBlogDAO microBlogDAO;
    private final UserDAO userDAO;

    public MicroBlogResource(MicroBlogDAO microBlogDAO, UserDAO userDAO) {
        this.microBlogDAO = microBlogDAO;
        this.userDAO = userDAO;
    }

    @GET
    public Response index(@PathParam("user") String userName) {
        User user = userDAO.findByName(userName);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND).build();
        }
        List<MicroBlog> microBlogs = microBlogDAO.findAllByUserId(user.getId());
        if (microBlogs.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.OK).entity(microBlogs).build();
    }

    @GET
    @Path("/{id}")
    public Response show(@PathParam("id") Integer id) {
        MicroBlog microBlog = microBlogDAO.findById(id);
        if (microBlog == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(microBlog).build();
    }

    @POST
    public Response create(@PathParam("user") String userName, @FormParam("content") String content) {
        User user = userDAO.findByName(userName);
        if (user == null) {
            return new ErrorResponse(Response.Status.NOT_FOUND).build();
        }
        MicroBlog microBlog = new MicroBlog(user.getId(), content);
        int id;
        try {
            id = microBlogDAO.create(microBlog);
        } catch (Exception e) {
            return ErrorResponse.fromException(e).build();
        }
        URI location = UriBuilder.fromPath(String.valueOf(id)).build();
        return Response.created(location).build();
    }
}
