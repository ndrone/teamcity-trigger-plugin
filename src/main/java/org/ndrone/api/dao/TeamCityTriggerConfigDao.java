package org.ndrone.api.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
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

    public TeamCityTriggerConfiguration[] find(Repository repository)
    {
        return activeObjects.find(TeamCityTriggerConfiguration.class,
            Query.select().where("REPOS_ID = ?", repository.getId()));
    }
}
