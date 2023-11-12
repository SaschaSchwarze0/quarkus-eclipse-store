package de.saschaschwarze.quarkuseclipsestore.exception;

public class ProductAlreadyExistsException extends RestException {

    public ProductAlreadyExistsException(String id) {
        super(409, "The product with the '" + id + "' identifier already exists.");
    }
}
