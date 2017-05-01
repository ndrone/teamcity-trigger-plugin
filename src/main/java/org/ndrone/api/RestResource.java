package org.ndrone.api;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.ndrone.api.model.TeamCity;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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
    private final UserManager userManager;
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
    public Response testConnection(final TeamCity teamCity, @Context HttpServletRequest request)
    {
        UserProfile user = userManager.getRemoteUser();
        if (user == null
            || !userManager.isSystemAdmin(user.getUserKey()))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpStatus statusCode;
        try
        {
            ResponseEntity<String> response = restTemplate
                    .exchange(chopTrailingSlash(teamCity.getUrl()) + "/httpAuth/app/rest/latest",
                            HttpMethod.GET, new HttpEntity<Object>(
                                    createHeaders(teamCity.getUsername(), teamCity.getPassword())),
                            String.class);
            statusCode = response.getStatusCode();
        }
        catch (HttpClientErrorException e)
        {
            statusCode = e.getStatusCode();
        }

        return Response.status(statusCode.value()).build();
    }

    private HttpHeaders createHeaders(final String username, final String password)
    {
        return new HttpHeaders()
        {
            {
                String auth = username
                    + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                String authHeader = "Basic "
                    + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
    }

    private String chopTrailingSlash(String url)
    {
        if (url.substring(url.length()
            - 1).equals("/"))
        {
            return StringUtils.chop(url);
        }
        return url;
    }
}
