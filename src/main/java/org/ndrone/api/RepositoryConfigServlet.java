package org.ndrone.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.ndrone.Utils;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
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
    private final UserManager              userManager;
    private final RepositoryService        repositoryService;
    private final TemplateRenderer         renderer;
    private final TeamCityTriggerConfigDao dao;

    @Autowired
    public RepositoryConfigServlet(@ComponentImport UserManager userManager,
        @ComponentImport RepositoryService repositoryService,
        @ComponentImport TemplateRenderer renderer, TeamCityTriggerConfigDao dao)
    {
        this.userManager = userManager;
        this.repositoryService = repositoryService;
        this.renderer = renderer;
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        if (!Utils.validateUser(userManager))
        {
            return;
        }

        Repository repository = getRepository(req);
        if (repository == null)
        {
            return;
        }
        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("repository", repository);
        TeamCityTriggerConfiguration[] teamCityTriggerConfigurations = dao.find(repository);
        contextMap.put("teamcity", teamCityTriggerConfigurations);
        contextMap.put("teamcitySize", teamCityTriggerConfigurations == null
            ? 0 : teamCityTriggerConfigurations.length);

        resp.setContentType("text/html;charset=utf-8");
        renderer.render("trigger.vm", contextMap, resp.getWriter());

    }

    private Repository getRepository(HttpServletRequest req) throws IOException
    {
        Repository repository;
        // Get repoSlug from path
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
