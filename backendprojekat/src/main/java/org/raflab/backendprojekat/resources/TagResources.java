package org.raflab.backendprojekat.resources;

import org.raflab.backendprojekat.dtos.response.TagResponse;
import org.raflab.backendprojekat.service.NewsService;
import org.raflab.backendprojekat.service.TagService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResources {

    @Inject
    private TagService tagService;

    @Inject
    private NewsService newsService;

    @GET
    public Response findAll() {
        List<TagResponse> tags = tagService.findAll();
        return Response.ok(tags).build();
    }

    @GET
    @Path("/{id}/news")
    public Response findNewsByTag(@PathParam("id") Long tagId,
                                  @QueryParam("page") @DefaultValue("1") int page) {
        return Response.ok(newsService.findByTag(tagId, page)).build();
    }
}
