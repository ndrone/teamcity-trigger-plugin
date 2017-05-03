package org.ndrone.api.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.DBParam;
import net.java.ao.Query;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Nicholas Drone on 5/1/17.
 */
@Scanned
@Named
public class TeamCityTriggerConfigDao
{
    @ComponentImport
    private final ActiveObjects activeObjects;

    @Inject
    public TeamCityTriggerConfigDao(ActiveObjects activeObjects)
    {
        this.activeObjects = activeObjects;
    }

    public TeamCityTriggerConfiguration[] find(int reposId)
    {
        return activeObjects.find(TeamCityTriggerConfiguration.class,
            Query.select().where("REPOS_ID = ?", reposId));
    }

    public TeamCityTriggerConfiguration get(int id)
    {
        return activeObjects.get(TeamCityTriggerConfiguration.class, id);
    }

    public TeamCityTriggerConfiguration save(int reposId, String buildConfigId, String username,
        String secret, String url)
    {
        return activeObjects.create(TeamCityTriggerConfiguration.class,
            new DBParam("REPOS_ID", reposId), new DBParam("BUILD_CONFIG_ID", buildConfigId),
            new DBParam("USERNAME", username), new DBParam("SECRET", secret),
            new DBParam("URL", url));
    }

    public void update(TeamCityTriggerConfiguration object)
    {
        object.save();
    }

    public void delete(TeamCityTriggerConfiguration object)
    {
        activeObjects.delete(object);
    }
}
