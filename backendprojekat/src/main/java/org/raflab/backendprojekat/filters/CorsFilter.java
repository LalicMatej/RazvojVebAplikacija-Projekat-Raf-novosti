package org.raflab.backendprojekat.filters;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) throws IOException {
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "http://localhost:5173"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true"
        );
    }
}
