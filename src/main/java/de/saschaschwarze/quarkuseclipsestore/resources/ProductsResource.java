package de.saschaschwarze.quarkuseclipsestore.resources;

import java.util.Collection;

import de.saschaschwarze.quarkuseclipsestore.data.Product;
import de.saschaschwarze.quarkuseclipsestore.exception.ProductNotFoundException;
import de.saschaschwarze.quarkuseclipsestore.exception.RestException;
import de.saschaschwarze.quarkuseclipsestore.services.ProductsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/products")
public class ProductsResource {

    @Inject
    ProductsService productsService;

    @POST
    public Response create(Product product, @Context UriInfo uriInfo) {
        try {
            this.productsService.add(product);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(product.getId()).build()).entity(product).build();
        } catch (RestException e) {
            return e.toResponse();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        try {
            this.productsService.deleteById(id);
            return Response.noContent().build();
        } catch (ProductNotFoundException e) {
            return e.toResponse();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        try {
            Product product = this.productsService.getById(id);
            return Response.ok(product).build();
        } catch (ProductNotFoundException e) {
            return e.toResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Product> list() {
        return this.productsService.getAll();
    }
}
