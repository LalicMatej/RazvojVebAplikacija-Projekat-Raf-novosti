package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.request.NewsRequest;
import org.raflab.backendprojekat.dtos.response.NewsResponse;
import org.raflab.backendprojekat.model.User;
import org.raflab.backendprojekat.service.NewsService;
import org.raflab.backendprojekat.service.ReactionService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NewsResource {

    @Inject
    private NewsService newsService;

    @Inject
    private ReactionService reactionService;

    @GET
    @Path("/latest")
    public Response findLatest() {
        return Response.ok(newsService.findLatest()).build();
    }

    @GET
    @Path("/most-read")
    public Response findMostRead() {
        return Response.ok(newsService.findMostRead()).build();
    }

    @GET
    @Path("/most-reacted")
    public Response findMostReacted() {
        return Response.ok(newsService.findMostReacted()).build();
    }

    @GET
    @Path("/search")
    public Response search(@QueryParam("query") String query,
                           @QueryParam("page") @DefaultValue("1") int page) {
        if (query == null || query.trim().isEmpty()) {
            return badRequest("Parametar 'query' je obavezan");
        }
        return Response.ok(newsService.search(query.trim(), page)).build();
    }

    @GET
    @Path("/by-category/{categoryId}")
    public Response findByCategory(@PathParam("categoryId") Long categoryId,
                                   @QueryParam("page") @DefaultValue("1") int page) {
        return Response.ok(newsService.findByCategory(categoryId, page)).build();
    }

    @GET
    @Path("/by-tag/{tagId}")
    public Response findByTag(@PathParam("tagId") Long tagId,
                              @QueryParam("page") @DefaultValue("1") int page) {
        return Response.ok(newsService.findByTag(tagId, page)).build();
    }

    @GET
    @Path("/{id}/related")
    public Response findRelated(@PathParam("id") Long id) {
        return Response.ok(newsService.findRelated(id)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id,
                             @Context HttpServletRequest httpRequest) {
        try {
            NewsResponse news = newsService.findById(id);
            String sessionId = httpRequest.getSession(true).getId();
            reactionService.recordVisit(id, sessionId);
            return Response.ok(news).build();
        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage());
        }
    }

    @GET
    public Response findAll(@QueryParam("page") @DefaultValue("1") int page) {
        return Response.ok(newsService.findAll(page)).build();
    }

    @POST
    public Response create(@Valid NewsRequest request,
                           @Context ContainerRequestContext requestContext) {
        User currentUser = (User) requestContext.getProperty("currentUser");
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(newsService.create(request, currentUser))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id,
                           @Valid NewsRequest request,
                           @Context ContainerRequestContext requestContext) {
        User currentUser = (User) requestContext.getProperty("currentUser");
        try {
            return Response.ok(newsService.update(id, request, currentUser)).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,
                           @Context ContainerRequestContext requestContext) {
        User currentUser = (User) requestContext.getProperty("currentUser");
        try {
            newsService.delete(id, currentUser);
            return Response.noContent().build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage())).build();
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
