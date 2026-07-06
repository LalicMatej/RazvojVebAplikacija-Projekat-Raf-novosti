package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.CategoryRequest;
import org.raflab.backendprojekat.service.CategoryService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResources {

    @Inject
    private CategoryService categoryService;

    @GET
    public Response findAll(@QueryParam("page") @DefaultValue("1") int page) {
        return Response.ok(categoryService.findAll(page)).build();
    }

    @GET
    @Path("/by-name")
    public Response findByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return badRequest("Parametar 'name' je obavezan");
        }
        try {
            return Response.ok(categoryService.findByName(name.trim())).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try {
            return Response.ok(categoryService.findById(id)).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @POST
    public Response create(@Valid CategoryRequest request) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(categoryService.create(request))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid CategoryRequest request) {
        try {
            return Response.ok(categoryService.update(id, request)).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            categoryService.delete(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    private Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", message)).build();
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }
}

