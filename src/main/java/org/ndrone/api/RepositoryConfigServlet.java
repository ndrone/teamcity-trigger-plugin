package org.ndrone.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.ndrone.api.service.TeamCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nicholas Drone on 4/28/17.
 */
@Component
public class RepositoryConfigServlet extends HttpServlet
{
    private final UserManager           userManager;
    private final RepositoryService     repositoryService;
    private final TemplateRenderer      renderer;
    private final TeamCityService       teamCityService;
    private final UserValidationService userValidationService;

    @Autowired
    public RepositoryConfigServlet(@ComponentImport UserManager userManager,
        @ComponentImport RepositoryService repositoryService,
        @ComponentImport TemplateRenderer renderer, TeamCityService teamCityService,
        UserValidationService userValidationService)
    {
        this.userManager = userManager;
        this.repositoryService = repositoryService;
        this.renderer = renderer;
        this.teamCityService = teamCityService;
        this.userValidationService = userValidationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        resp.setContentType("text/html;charset=utf-8");
        Map<String, Object> contextMap = new HashMap<>();

        Repository repository = getRepository(req);
        if (repository == null)
        {
            contextMap.put("errorMessage", "Repository not found!");
            renderer.render("error.vm", contextMap, resp.getWriter());
        }
        else
        {
            if (!userValidationService.isUserRepositoryAdmin(userManager.getRemoteUser(),
                repository))
            {
                contextMap.put("errorMessage",
                    "User doesn't have enough permission to edit settings.");
                renderer.render("error.vm", contextMap, resp.getWriter());
            }

            contextMap.put("repository", repository);
            contextMap.put("teamcity", teamCityService.find(repository));
            renderer.render("trigger.vm", contextMap, resp.getWriter());
        }
    }

    private Repository getRepository(HttpServletRequest req) throws IOException
    {
        Repository repository;
        String[] components = req.getPathInfo().split("/");
        if (components.length < 3)
        {
            repository = null;
        }
        else
        {
            repository = repositoryService.getBySlug(components[1], components[3]);
        }

        return repository;
    }
}
