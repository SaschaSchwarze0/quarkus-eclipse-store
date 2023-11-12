package de.saschaschwarze.quarkuseclipsestore.exception;

import jakarta.ws.rs.core.Response;

public abstract class RestException extends Exception {

    private final int statusCode;
    
    protected RestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Response toResponse() {
        return Response.status(this.statusCode).entity(new RestExceptionBody(this.statusCode, this.getMessage())).build();
    }
}
