package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.CommentRequest;
import org.raflab.backendprojekat.dtos.request.ReactionRequest;
import org.raflab.backendprojekat.service.CommentService;
import org.raflab.backendprojekat.service.ReactionService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

@Path("/news/{newsId}/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {

    @Inject
    private CommentService commentService;

    @Inject
    private ReactionService reactionService;

    @GET
    public Response findByNews(@PathParam("newsId") Long newsId,
                               @QueryParam("page") @DefaultValue("1") int page) {
        try {
            return Response.ok(commentService.findByNews(newsId, page)).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @POST
    public Response create(@PathParam("newsId") Long newsId,
                           @Valid CommentRequest request) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(commentService.create(newsId, request))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{commentId}")
    public Response delete(@PathParam("newsId") Long newsId,
                           @PathParam("commentId") Long commentId) {
        try {
            commentService.delete(commentId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @GET
    @Path("/{commentId}/reactions")
    public Response getReactions(@PathParam("newsId") Long newsId,
                                 @PathParam("commentId") Long commentId,
                                 @Context HttpServletRequest httpRequest) {
        String sessionId = httpRequest.getSession(true).getId();
        try {
            return Response.ok(reactionService.getCommentReactions(commentId, sessionId)).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @POST
    @Path("/{commentId}/reactions")
    public Response react(@PathParam("newsId") Long newsId,
                          @PathParam("commentId") Long commentId,
                          @Valid ReactionRequest request,
                          @Context HttpServletRequest httpRequest) {
        String sessionId = httpRequest.getSession(true).getId();
        try {
            return Response.ok(reactionService.reactToComment(commentId, sessionId, request)).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    private Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", message)).build();
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }
}
