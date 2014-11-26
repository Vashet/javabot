package javabot.web;

import com.sun.jersey.api.core.HttpContext;
import javabot.web.model.OAuthConfig;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger log = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    @Context
    HttpContext httpContext;
    private JavabotConfiguration configuration;

    public RuntimeExceptionMapper(final JavabotConfiguration configuration) {

        this.configuration = configuration;
    }

    @Override
    public Response toResponse(RuntimeException runtime) {

        // Build default response
        Response defaultResponse = Response
                                       .serverError()
//                                       .entity(new PublicErrorResource().view500())
                                       .build();

        // Check for any specific handling
        if (runtime instanceof WebApplicationException) {
            return handleWebApplicationException(runtime, defaultResponse);
        }

        // Use the default
        log.error(runtime.getMessage(), runtime);
        return defaultResponse;

    }

    private Response handleWebApplicationException(RuntimeException exception, Response defaultResponse)  {
        WebApplicationException webAppException = (WebApplicationException) exception;

        // No logging
        int status = webAppException.getResponse().getStatus();
        if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
            try {
                Response build = Response
                                     .status(Status.TEMPORARY_REDIRECT)
                                     .location(new URI("/auth/login"))
                                     .build();
                return build;
            } catch (URISyntaxException e) {
                return Response
                                       .status(Status.INTERNAL_SERVER_ERROR)
                //                       .entity(new PublicErrorResource().view500())
                                       .build();            }
        } else if (status == Status.FORBIDDEN.getStatusCode()) {
            return Response
                       .status(Response.Status.FORBIDDEN)
//                       .entity(new PublicErrorResource().view403())
                       .build();
        } else if (status == Status.NOT_FOUND.getStatusCode()) {
            return Response
                       .status(Response.Status.NOT_FOUND)
//                       .entity(new PublicErrorResource().view404())
                       .build();
        } else {
            log.error(exception.getMessage(), exception);
            return defaultResponse;
        }
    }

    /**
     * Gets an initialized SocialAuthManager
     *
     * @return gets an initialized SocialAuthManager
     */
    private SocialAuthManager getSocialAuthManager() {
        SocialAuthConfig config = SocialAuthConfig.getDefault();
        try {
            config.load(configuration.getOAuthCfgProperties());
            SocialAuthManager manager = new SocialAuthManager();
            manager.setSocialAuthConfig(config);
            return manager;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
