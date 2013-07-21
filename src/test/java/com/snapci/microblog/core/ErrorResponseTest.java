package com.snapci.microblog.core;

import com.snapci.microblog.Constants;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ErrorResponseTest {

    @Test
    public void testFromException() throws Exception {
        Exception e = new Exception("Blew up");
        ErrorResponse errorResponse = ErrorResponse.fromException(e);
        Response response = errorResponse.build();
        assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    public void testFromExceptionWithUniqueConstraintViolation() throws Exception {
        SQLException cause = new SQLException("Duplicate key", Constants.UNIQUE_VIOLATION_SQL_STATE, 23505);
        ErrorResponse errorResponse = ErrorResponse.fromException(new Exception(cause));
        Response response = errorResponse.build();
        assertThat(response.getStatus(), equalTo(Response.Status.CONFLICT.getStatusCode()));
    }

    @Test
    public void testBuild() throws Exception {
        Exception e = new Exception("Blew up");
        ErrorResponse errorResponse = ErrorResponse.fromException(e);
        Response response = errorResponse.build();
        assertThat((ErrorResponse) response.getEntity(), equalTo(errorResponse));

    }
}
