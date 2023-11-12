package de.saschaschwarze.quarkuseclipsestore.exception;

public class RestExceptionBody {

    private final int statusCode;
    private final String message;

    public RestExceptionBody(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "{" +
            " statusCode='" + getStatusCode() + "'" +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
