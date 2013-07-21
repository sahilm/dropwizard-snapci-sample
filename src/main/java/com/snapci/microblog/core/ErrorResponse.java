package com.snapci.microblog.core;

import com.snapci.microblog.Constants;

import javax.ws.rs.core.Response;
import java.sql.SQLException;

public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(Response.Status status, String message) {
        this.status = status.getStatusCode();
        this.message = message;
    }

    public ErrorResponse(Response.Status status) {
        this(status, status.toString());
    }

    public static ErrorResponse fromException(Exception e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            String sqlState = ((SQLException) cause).getSQLState();
            if (sqlState.equals(Constants.UNIQUE_VIOLATION_SQL_STATE)) {
                status = Response.Status.CONFLICT;
            }
        }
        return new ErrorResponse(status, status.toString());
    }

    public Response build() {
        return Response
                .status(getStatus())
                .entity(this)
                .build();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
