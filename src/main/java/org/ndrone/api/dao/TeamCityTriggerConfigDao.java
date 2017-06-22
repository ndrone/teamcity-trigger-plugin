package org.ndrone.api.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.util.Assert;

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
        Assert.notNull(activeObjects, "ActiveObjects must not be null");
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

    public TeamCityTriggerConfiguration save(int reposId, String username, String secret,
        String salt, String url, String buildConfigId, String buildConfigName)
    {
        return activeObjects.create(TeamCityTriggerConfiguration.class,
            new DBParam("REPOS_ID", reposId), new DBParam("USERNAME", username),
            new DBParam("SECRET", secret), new DBParam("SALT", salt), new DBParam("URL", url),
            new DBParam("BUILD_CONFIG_ID", buildConfigId),
            new DBParam("BUILD_CONFIG_NAME", buildConfigName));
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
