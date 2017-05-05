package org.ndrone.api.service;

import org.ndrone.api.TeamCity;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.bitbucket.repository.Repository;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@Component
public class TeamCityServiceImpl implements TeamCityService
{
    private final TeamCityTriggerConfigDao dao;

    @Autowired
    public TeamCityServiceImpl(TeamCityTriggerConfigDao dao)
    {
        this.dao = dao;
    }

    public TeamCity find(Repository repository)
    {
        TeamCity teamCity;
        TeamCityTriggerConfiguration[] objects = dao.find(repository.getId());
        if (objects.length == 0)
        {
            teamCity = new TeamCity.Builder().withId(String.valueOf(repository.getId())).build();
        }
        else
        {
            teamCity = buildTeamCity(objects[0]);
        }
        return teamCity;
    }

    private TeamCity buildTeamCity(TeamCityTriggerConfiguration configuration)
    {
        return new TeamCity.Builder().withId(String.valueOf(configuration.getReposId()))
            .withBuildConfigId(configuration.getBuildConfigId())
            .withUsername(configuration.getUsername()).withPassword(configuration.getSecret())
            .withUrl(configuration.getUrl()).withBuildConfigName(configuration.getBuildConfigName())
            .build();
    }

    public void save(TeamCity teamCity)
    {
        TeamCityTriggerConfiguration[] objects = dao.find(Integer.parseInt(teamCity.getId()));
        if (objects.length == 0)
        {
            // object doesn't exist yet
            dao.save(Integer.parseInt(teamCity.getId()), teamCity.getBuildConfigId(),
                teamCity.getUsername(), teamCity.getPassword(), teamCity.getUrl(),
                teamCity.getBuildConfigName());
        }
        else
        {
            // do we need to update it
            TeamCity databaseObject = buildTeamCity(objects[0]);
            if (!databaseObject.equals(teamCity))
            {
                TeamCityTriggerConfiguration configuration = objects[0];
                configuration.setReposId(Integer.valueOf(teamCity.getId()));
                configuration.setBuildConfigId(teamCity.getBuildConfigId());
                configuration.setUsername(teamCity.getUsername());
                configuration.setSecret(teamCity.getPassword());
                configuration.setUrl(teamCity.getUrl());
                configuration.setBuildConfigName(teamCity.getBuildConfigName());
                dao.update(configuration);
            }
        }
    }

    public void delete(TeamCity teamCity)
    {
        TeamCityTriggerConfiguration[] objects = dao.find(Integer.parseInt(teamCity.getId()));
        if (objects.length > 0)
        {
            dao.delete(objects[0]);
        }
    }
}
