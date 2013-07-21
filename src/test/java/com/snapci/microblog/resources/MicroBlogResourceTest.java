package com.snapci.microblog.resources;

import com.snapci.microblog.core.MicroBlog;
import com.snapci.microblog.core.User;
import com.snapci.microblog.jdbi.MicroBlogDAO;
import com.snapci.microblog.jdbi.UserDAO;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MicroBlogResourceTest {
    private final UserDAO userDAO = mock(UserDAO.class);
    private final MicroBlogDAO microBlogDAO = mock(MicroBlogDAO.class);
    private final MicroBlogResource microBlogResource = new MicroBlogResource(microBlogDAO, userDAO);

    @Test
    public void testIndexReturnsNotFoundIfThereIsNoUser() throws Exception {
        assertThat(microBlogResource.index("notthere").getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void testIndexReturnsNoContentIfThereAreNoMicroBlogs() throws Exception {
        when(userDAO.findByName("foo")).thenReturn(new User("foo"));
        assertThat(microBlogResource.index("foo").getStatus(), equalTo(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void testIndexReturnsAllMicroBlogs() throws Exception {
        when(userDAO.findByName("foo")).thenReturn(new User(1, "foo"));
        List<MicroBlog> expected = Arrays.asList(new MicroBlog(1, "Hey There"), new MicroBlog(1, "Foo"));
        when(microBlogDAO.findAllByUserId(1)).thenReturn(expected);
        @SuppressWarnings("unchecked")
        List<MicroBlog> actual = (List<MicroBlog>) microBlogResource.index("foo").getEntity();
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testShowReturnsNotFoundIfBlogDoesNotExist() throws Exception {
        assertThat(microBlogResource.show(1).getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()));

    }

    @Test
    public void testShowReturnsBlog() throws Exception {
        MicroBlog expected = new MicroBlog(1, "hello");
        when(microBlogDAO.findById(1)).thenReturn(expected);
        @SuppressWarnings("unchecked")
        MicroBlog actual = (MicroBlog) microBlogResource.show(1).getEntity();
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testCreateReturnsNotFoundIfUserDoesNotExist() throws Exception {
        Response actual = microBlogResource.create("notthere", "yep");
        assertThat(actual.getStatus(), equalTo(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void testCreate() throws Exception {
        User user = new User(1, "sahil");
        MicroBlog microBlog = new MicroBlog(user.getId(), "hey");
        when(userDAO.findByName(user.getName())).thenReturn(user);
        when(microBlogDAO.create(microBlog)).thenReturn(1);
        Response expected = Response.created(UriBuilder.fromPath("1").build()).build();
        Response actual = microBlogResource.create(user.getName(), microBlog.getContent());
        assertThat(actual.getMetadata().toString(), equalTo(expected.getMetadata().toString()));
        assertThat(actual.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
    }
}
