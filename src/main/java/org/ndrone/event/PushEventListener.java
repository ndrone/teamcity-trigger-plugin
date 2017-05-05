package org.ndrone.event;

import org.ndrone.Utils;
import org.ndrone.api.TeamCity;
import org.ndrone.api.service.TeamCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.atlassian.bitbucket.event.repository.RepositoryPushEvent;
import com.atlassian.event.api.EventListener;

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
    {
        TeamCity teamCity = teamCityService.find(pushEvent.getRepository());
        if (teamCity.getBuildConfigId() != null)
        {
            HttpHeaders headers = Utils.createHeaders(teamCity.getUsername(),
                teamCity.getPassword());
            headers.add("Accept", "application/json");
            headers.setOrigin(teamCity.getUrl());

            BuildType buildType = new BuildType(teamCity.getBuildConfigId());

            restTemplate.exchange(Utils.chopTrailingSlash(teamCity.getUrl()), HttpMethod.POST,
                new HttpEntity<BuildType>(buildType, headers), String.class);
        }
    }
}
