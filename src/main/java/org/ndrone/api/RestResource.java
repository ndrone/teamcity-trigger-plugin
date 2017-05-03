package org.ndrone.api;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import org.ndrone.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Nicholas Drone on 4/29/17.
 */
@Path("/")
@Scanned
public class RestResource
{
    @ComponentImport
    private final UserManager  userManager;
    private final RestTemplate restTemplate;

    @Inject
    public RestResource(UserManager userManager)
    {
        this.userManager = userManager;
        this.restTemplate = new RestTemplate();
    }

    public RestResource(UserManager userManager, RestTemplate restTemplate)
    {
        this.userManager = userManager;
        this.restTemplate = restTemplate;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response testConnection(final TeamCity teamCity)
    {

        if (!Utils.validateUser(userManager))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpStatus statusCode;
        try
        {
            ResponseEntity<String> response = restTemplate
                .exchange(Utils.chopTrailingSlash(teamCity.getUrl())
                    + "/httpAuth/app/rest/latest",
                    HttpMethod.GET,
                    new HttpEntity<Object>(
                        Utils.createHeaders(teamCity.getUsername(), teamCity.getPassword())),
                    String.class);
            statusCode = response.getStatusCode();
        }
        catch (HttpClientErrorException e)
        {
            statusCode = e.getStatusCode();
        }

        return Response.status(statusCode.value()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/save")
    public Response save(final TeamCity teamCity)
    {
        if (!Utils.validateUser(userManager))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.status(Response.Status.OK).build();
    }
}
