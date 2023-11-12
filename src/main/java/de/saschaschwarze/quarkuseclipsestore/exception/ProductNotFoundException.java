package de.saschaschwarze.quarkuseclipsestore.exception;

public class ProductNotFoundException extends RestException {

    public ProductNotFoundException(String id) {
        super(404, "The product with the '" + id + "' identifier does not exist.");
    }
}
