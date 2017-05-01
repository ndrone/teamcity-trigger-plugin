package org.ndrone.api;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.apache.commons.lang3.StringUtils;
import org.ndrone.api.model.TeamCity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    @Inject
    public RestResource(UserManager userManager)
    {
        this.userManager = userManager;
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

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(chopTrailingSlash(teamCity.getUrl())
            + "/httpAuth/app/rest/latest", HttpMethod.GET, null, String.class);

        return Response.status(response.getStatusCodeValue()).build();
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
