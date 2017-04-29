package org.ndrone.api;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.ndrone.api.model.TeamCity;

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
    private final UserManager           userManager;

    @Inject
    public RestResource(UserManager userManager)
    {
        this.userManager = userManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testConnection(final TeamCity teamCity, @Context HttpServletRequest request)
    {
        UserProfile user = userManager.getRemoteUser();
        if (user == null || !userManager.isSystemAdmin(user.getUserKey()))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok().build();
    }
}
