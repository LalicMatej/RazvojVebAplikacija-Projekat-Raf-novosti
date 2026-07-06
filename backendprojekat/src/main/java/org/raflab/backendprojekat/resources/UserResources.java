package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.UpdateUserRequest;
import org.raflab.backendprojekat.dtos.request.UserRequest;
import org.raflab.backendprojekat.dtos.response.PageResponse;
import org.raflab.backendprojekat.dtos.response.UserResponse;
import org.raflab.backendprojekat.service.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {

    @Inject
    private UserService userService;

    @GET
    public Response findAll(@QueryParam("page") @DefaultValue("1") int page) {
        PageResponse<UserResponse> result = userService.findAll(page);
        return Response.ok(result).build();
    }

    @GET
    @Path("/by-email")
    public Response findByEmail(@QueryParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return badRequest("Parametar 'email' je obavezan");
        }
        try {
            return Response.ok(userService.findByEmail(email.trim())).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try {
            return Response.ok(userService.findById(id)).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @POST
    public Response create(@Valid UserRequest request) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(userService.create(request))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UpdateUserRequest request) {
        try {
            return Response.ok(userService.update(id, request)).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id) {
        try {
            return Response.ok(userService.updateStatus(id)).build();
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
