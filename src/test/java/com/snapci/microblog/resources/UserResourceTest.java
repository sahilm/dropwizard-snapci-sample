package com.snapci.microblog.resources;

import com.snapci.microblog.Constants;
import com.snapci.microblog.core.ErrorResponse;
import com.snapci.microblog.core.User;
import com.snapci.microblog.jdbi.UserDAO;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.sql.SQLException;

import static javax.ws.rs.core.Response.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {
    private final UserDAO dao = mock(UserDAO.class);
    private final UserResource userResource = new UserResource(dao);


    @Test
    public void testCreate() throws Exception {
        User user = new User("sahil");
        Response expected = Response.created(UriBuilder.fromPath(user.getName().toLowerCase()).build()).build();
        Response actual = userResource.create(user.getName());
        assertThat(actual.getMetadata().toString(), equalTo(expected.getMetadata().toString()));
        assertThat(actual.getStatus(), equalTo(Status.CREATED.getStatusCode()));
        verify(dao).create(user);
    }

    @Test
    public void testCreateWithDuplicateNameCallsErrorResponseFromException() throws Exception {
        ErrorResponse errorResponse = mock(ErrorResponse.class);
        User user = new User("foo");
        SQLException cause = new SQLException("Duplicate key", Constants.UNIQUE_VIOLATION_SQL_STATE, 23505);
        when(dao.create(user)).thenThrow(new RuntimeException(cause));
        assertThat(userResource.create("foo").getStatus(), equalTo(Status.CONFLICT.getStatusCode()));
    }

    @Test
    public void testShowReturnsRightUser() throws Exception {
        User user = new User(1, "sahil");
        when(dao.findByName("sahil")).thenReturn(user);
        Response expected = Response.status(Status.OK).entity(user).build();
        assertThat(userResource.show("sahil").getEntity(), equalTo(expected.getEntity()));
        verify(dao).findByName("sahil");
    }

}
