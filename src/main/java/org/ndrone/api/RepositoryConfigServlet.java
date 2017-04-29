package org.ndrone.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
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
    private final UserManager       userManager;
    private final RepositoryService repositoryService;
    private final TemplateRenderer  renderer;

    @Autowired
    public RepositoryConfigServlet(@ComponentImport UserManager userManager,
        @ComponentImport RepositoryService repositoryService, @ComponentImport TemplateRenderer renderer)
    {
        this.userManager = userManager;
        this.repositoryService = repositoryService;
        this.renderer = renderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        if (!validateUser())
        {
            return;
        }

        Repository repository = getRepository(req, resp);
        if (repository == null)
        {
            return;
        }
        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("repository", repository);


        resp.setContentType("text/html;charset=utf-8");
        renderer.render("admin.vm", contextMap, resp.getWriter());

    }

    private boolean validateUser()
    {
        UserProfile user = userManager.getRemoteUser();
        return !(user == null
            || !userManager.isSystemAdmin(user.getUserKey()));
    }

    private Repository getRepository(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        // Get repoSlug from path
        String pathInfo = req.getPathInfo();

        String[] components = pathInfo.split("/");

        if (components.length < 3)
        {
            return null;
        }

        Repository repository = repositoryService.getBySlug(components[1], components[2]);
        if (repository == null)
        {
            return null;
        }
        return repository;
    }
}
