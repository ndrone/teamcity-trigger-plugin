package org.ndrone.api;

import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import org.ndrone.Utils;
import org.ndrone.api.service.TeamCityService;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Nicholas Drone on 4/29/17.
 */
@Path("/")
@Scanned
public class RestResource
{
    public static final String AUTH_URL_CHECK = "/httpAuth/app/rest/latest";
    public static final String FETCH_BUILDTYPES_URL = "/httpAuth/app/rest/buildTypes";

    @ComponentImport
    private final UserManager           userManager;
    @ComponentImport
    private final RepositoryService     repositoryService;

    private final RestTemplate          restTemplate;

    private final TeamCityService       teamCityService;
    private final UserValidationService userValidationService;

    @Inject
    public RestResource(UserManager userManager, RepositoryService repositoryService,
        TeamCityService teamCityService, UserValidationService userValidationService)
    {
        this.userManager = userManager;
        this.repositoryService = repositoryService;
        this.teamCityService = teamCityService;
        this.userValidationService = userValidationService;
        this.restTemplate = new RestTemplate();
    }

    public RestResource(UserManager userManager, RepositoryService repositoryService,
        TeamCityService teamCityService, UserValidationService userValidationService,
        RestTemplate restTemplate)
    {
        this.userManager = userManager;
        this.repositoryService = repositoryService;
        this.teamCityService = teamCityService;
        this.userValidationService = userValidationService;
        this.restTemplate = restTemplate;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response testConnection(final TeamCity teamCity)
    {

        if (!validateUser(teamCity))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpStatus statusCode;
        try
        {
            ResponseEntity<String> response = restTemplate
                .exchange(Utils.chopTrailingSlash(teamCity.getUrl())
                    + AUTH_URL_CHECK, HttpMethod.GET,
                    new HttpEntity<>(Utils.createHeaders(teamCity.getUsername(),
                        teamCityService.comparePassword(teamCity))),
                    String.class);
            statusCode = response.getStatusCode();
        }
        catch (HttpClientErrorException e)
        {
            statusCode = e.getStatusCode();
        }
        catch (Exception e)
        {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return Response.status(statusCode.value()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/save")
    public Response save(final TeamCity teamCity)
    {
        if (!validateUser(teamCity))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try
        {
            teamCityService.save(teamCity);
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fetchBuilds")
    public Response fetchBuilds(final TeamCity teamCity)
    {
        if (!validateUser(teamCity))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpStatus statusCode;
        BuildTypes buildTypes;
        HttpHeaders headers;
        try
        {
            headers = Utils.createHeaders(teamCity.getUsername(),
                teamCityService.comparePassword(teamCity));
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        headers.add("Accept", "application/json");
        try
        {
            ResponseEntity<BuildTypes> response = restTemplate
                .exchange(Utils.chopTrailingSlash(teamCity.getUrl())
                    + FETCH_BUILDTYPES_URL, HttpMethod.GET,
                    new HttpEntity<>(headers), BuildTypes.class);
            statusCode = response.getStatusCode();
            buildTypes = response.getBody();
        }
        catch (HttpClientErrorException e)
        {
            statusCode = e.getStatusCode();
            buildTypes = new BuildTypes();
        }

        return Response.status(statusCode.value()).entity(buildTypes).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public Response delete(final TeamCity teamCity)
    {
        if (!validateUser(teamCity))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        teamCityService.delete(teamCity);
        return Response.status(Response.Status.OK).build();
    }

    private boolean validateUser(final TeamCity teamCity)
    {
        return userValidationService.isUserRepositoryAdmin(userManager.getRemoteUser(),
            repositoryService.getById(Integer.parseInt(teamCity.getId())));

    }
}
