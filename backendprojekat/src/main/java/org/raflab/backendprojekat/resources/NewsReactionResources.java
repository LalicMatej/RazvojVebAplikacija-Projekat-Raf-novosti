package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.ReactionRequest;
import org.raflab.backendprojekat.dtos.response.ReactionResponse;
import org.raflab.backendprojekat.service.ReactionService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/news/{newsId}/reactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NewsReactionResources {

    @Inject
    private ReactionService reactionService;

    @GET
    public Response getReactions(@PathParam("newsId") Long newsId,
                                 @Context HttpServletRequest httpRequest) {
        String sessionId = httpRequest.getSession(true).getId();
        try {
            ReactionResponse response = reactionService.getNewsReactions(newsId, sessionId);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @POST
    public Response react(@PathParam("newsId") Long newsId,
                          @Valid ReactionRequest request,
                          @Context HttpServletRequest httpRequest) {
        String sessionId = httpRequest.getSession(true).getId();
        try {
            ReactionResponse response = reactionService.reactToNews(newsId, sessionId, request);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    private Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("message", message))
                .build();
    }
}

