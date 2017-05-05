package org.ndrone.api.service;

import com.atlassian.bitbucket.repository.Repository;
import org.ndrone.SecurityUtils;
import org.ndrone.api.TeamCity;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        TeamCityTriggerConfiguration configuration = getConfiguration(repository);
        if (configuration == null)
        {
            teamCity = new TeamCity.Builder().withId(String.valueOf(repository.getId())).build();
        }
        else
        {
            teamCity = buildTeamCity(configuration);
        }
        return teamCity;
    }

    public TeamCityTriggerConfiguration getConfiguration(Repository repository)
    {
        TeamCityTriggerConfiguration[] objects = dao.find(repository.getId());
        return (objects.length == 0)
            ? null : objects[0];
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
        throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
        BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException
    {
        TeamCityTriggerConfiguration[] objects = dao.find(Integer.parseInt(teamCity.getId()));
        if (objects.length == 0)
        {
            // object doesn't exist yet
            String salt = SecurityUtils.generateSalt();
            dao.save(Integer.parseInt(teamCity.getId()), teamCity.getUsername(),
                SecurityUtils.encrypt(salt, teamCity.getPassword()), salt, teamCity.getUrl(),
                teamCity.getBuildConfigId(), teamCity.getBuildConfigName());
        }
        else
        {
            // do we need to update it
            TeamCity databaseObject = buildTeamCity(objects[0]);
            if (!databaseObject.equals(teamCity))
            {
                TeamCityTriggerConfiguration configuration = objects[0];
                configuration.setReposId(Integer.valueOf(teamCity.getId()));
                configuration.setUsername(teamCity.getUsername());
                configuration.setUrl(teamCity.getUrl());
                configuration.setBuildConfigId(teamCity.getBuildConfigId());
                configuration.setBuildConfigName(teamCity.getBuildConfigName());

                if (!configuration.getSecret().equals(teamCity.getPassword()))
                {
                    String salt = SecurityUtils.generateSalt();
                    configuration.setSalt(salt);
                    configuration.setSecret(SecurityUtils.encrypt(salt, teamCity.getPassword()));
                }
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
