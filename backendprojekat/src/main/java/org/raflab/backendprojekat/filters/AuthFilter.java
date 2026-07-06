package org.raflab.backendprojekat.filters;

import org.raflab.backendprojekat.model.User;
import org.raflab.backendprojekat.resources.*;
import org.raflab.backendprojekat.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private UserService userService;

    // -------------------------------------------------------------------------
    // Javni endpointi (bez tokena)
    // -------------------------------------------------------------------------

    private boolean isPublic(String path, String method, List<Object> matchedResources) {
        // Auth endpoints
        if (path.contains("auth/login") || path.contains("auth/logout")) {
            return true;
        }

        for (Object resource : matchedResources) {
            // Sve GET operacije na vestima su javne
            if (resource instanceof NewsResource && method.equals("GET")) {
                return true;
            }

            // GET komentara i POST novog komentara su javni
            if (resource instanceof CommentResource && (method.equals("GET") || method.equals("POST"))) {
                return true;
            }

            // Reakcije na vesti su javne
            if (resource instanceof NewsReactionResources) {
                return true;
            }


            if (resource instanceof CommentResource && method.equals("POST")
                    && path.matches(".*comments/\\d+/reactions.*")) {
                return true;
            }
            if (resource instanceof CommentResource && method.equals("GET")
                    && path.matches(".*comments/\\d+/reactions.*")) {
                return true;
            }

            // Tagovi - samo citanje je javno
            if (resource instanceof TagResources && method.equals("GET")) {
                return true;
            }

            // Kategorije - samo citanje je javno
            if (resource instanceof CategoryResources && method.equals("GET")) {
                return true;
            }
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Endpointi koji zahtevaju ADMIN rolu
    // -------------------------------------------------------------------------

    private boolean isAdminOnly(String path, List<Object> matchedResources) {
        for (Object resource : matchedResources) {
            if (resource instanceof UserResources) {
                return true;
            }

            if (resource instanceof CommentResource
                    && path.matches(".*comments/\\d+$")) {
                return true;
            }
        }
        return false;
    }



    private User extractUser(ContainerRequestContext requestContext) {
        String header = requestContext.getHeaderString("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.replace("Bearer ", "");
        return userService.getAuthenticatedUser(token);
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String message) {
        ctx.abortWith(Response.status(status)
                .entity("{\"message\":\"" + message + "\"}")
                .build());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path    = requestContext.getUriInfo().getPath();
        String method  = requestContext.getMethod();
        List<Object> matchedResources = requestContext.getUriInfo().getMatchedResources();

        // CORS preflight zahtevi moraju proci bez autentikacije.
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        // Javni endpointi - preskacemo proveru
        if (isPublic(path, method, matchedResources)) {
            return;
        }

        User currentUser = extractUser(requestContext);
        if (currentUser == null) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Niste prijavljeni");
            return;
        }

        if (!currentUser.isActive()) {
            abort(requestContext, Response.Status.FORBIDDEN, "Vaš nalog je deaktiviran");
            return;
        }

        // Endpointi koji zahtevaju ADMIN rolu
        if (isAdminOnly(path, matchedResources) && !currentUser.isAdmin()) {
            abort(requestContext, Response.Status.FORBIDDEN, "Nemate dozvolu za ovu akciju");
            return;
        }

        requestContext.setProperty("currentUser", currentUser);
    }
}
