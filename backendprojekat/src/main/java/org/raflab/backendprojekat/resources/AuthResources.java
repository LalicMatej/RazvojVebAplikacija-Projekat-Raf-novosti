package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.LoginRequest;
import org.raflab.backendprojekat.service.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResources {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        try {
            Map<String, String> response = userService.login(request);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return Response.ok(Map.of("message", "Uspešno ste se odjavili")).build();
    }
}
