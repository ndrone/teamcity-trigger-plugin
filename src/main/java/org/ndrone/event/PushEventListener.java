package org.ndrone.event;

import org.ndrone.SecurityUtils;
import org.ndrone.Utils;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.ndrone.api.service.TeamCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.atlassian.bitbucket.event.repository.RepositoryPushEvent;
import com.atlassian.event.api.EventListener;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nicholas Drone on 5/5/17.
 */
@Component
public class PushEventListener
{
    private final TeamCityService teamCityService;
    private final RestTemplate    restTemplate;

    @Autowired
    public PushEventListener(TeamCityService teamCityService)
    {
        this.teamCityService = teamCityService;
        this.restTemplate = new RestTemplate();
    }

    public PushEventListener(TeamCityService teamCityService, RestTemplate restTemplate)
    {
        this.teamCityService = teamCityService;
        this.restTemplate = restTemplate;
    }

    @EventListener
    public void pushEvent(RepositoryPushEvent pushEvent)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException
    {
        TeamCityTriggerConfiguration configuration = teamCityService
            .getConfiguration(pushEvent.getRepository());
        if (configuration.getBuildConfigId() != null)
        {
            HttpHeaders headers = Utils.createHeaders(configuration.getUsername(),
                SecurityUtils.decrypt(configuration.getSalt(), configuration.getSecret()));
            headers.add("Accept", "application/json");
            headers.setOrigin(configuration.getUrl());

            BuildType buildType = new BuildType(configuration.getBuildConfigId());

            restTemplate.exchange(Utils.chopTrailingSlash(configuration.getUrl()), HttpMethod.POST,
                new HttpEntity<BuildType>(buildType, headers), String.class);
        }
    }
}
