package org.ndrone.event;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.repository.RepositoryRefsChangedEvent;
import com.atlassian.bitbucket.pull.PullRequestAction;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.RefChangeType;
import com.atlassian.event.api.EventListener;
import org.ndrone.SecurityUtils;
import org.ndrone.Utils;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.ndrone.api.service.TeamCityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nicholas Drone on 5/5/17.
 */
@Component
public class GitEventListener
{
    private final Logger          log = LoggerFactory.getLogger(GitEventListener.class);

    private final TeamCityService teamCityService;
    private final RestTemplate    restTemplate;

    @Autowired
    public GitEventListener(TeamCityService teamCityService)
    {
        this.teamCityService = teamCityService;
        this.restTemplate = new RestTemplate();
    }

    public GitEventListener(TeamCityService teamCityService, RestTemplate restTemplate)
    {
        this.teamCityService = teamCityService;
        this.restTemplate = restTemplate;
    }

    @EventListener
    public void changeEvent(RepositoryRefsChangedEvent event)
        throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        log.info("RepositoryRefsChangedEvent triggering");
        TeamCityTriggerConfiguration configuration = teamCityService
            .getConfiguration(event.getRepository());
        if (configuration != null
            && configuration.getBuildConfigId() != null)
        {
            HttpHeaders headers = getHttpHeaders(configuration);

            for (RefChange refChange : event.getRefChanges())
            {
                log.info("Type: {} Reference id: {} displayId: {} typeChange: {}",
                    refChange.getType().name(), refChange.getRef().getId(),
                    refChange.getRef().getDisplayId(), refChange.getRef().getType().toString());

                if (validForTrigger(refChange))
                {
                    log.info("Trigger a build");
                    BuildType buildType = new BuildType(configuration.getBuildConfigId());
                    Build build = new Build(refChange.getRef().getDisplayId(), buildType);
                    queueBuild(configuration, headers, build);
                }
            }

        }
    }

    @EventListener
    public void pullRequestEvent(PullRequestEvent event)
        throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        log.info("PullRequestOpenedEvent triggering");
        TeamCityTriggerConfiguration configuration = teamCityService
            .getConfiguration(event.getPullRequest().getToRef().getRepository());
        if (configuration != null
            && configuration.getBuildConfigId() != null && validForTrigger(event))
        {
            HttpHeaders headers = getHttpHeaders(configuration);

            BuildType buildType = new BuildType(configuration.getBuildConfigId());
            Build build = new Build("pull-requests/"
                + event.getPullRequest().getId() + "/from", buildType);
            queueBuild(configuration, headers, build);
        }
    }

    private void queueBuild(TeamCityTriggerConfiguration configuration, HttpHeaders headers,
        Build build)
    {
        try
        {
            ResponseEntity<String> exchange = restTemplate
                .exchange(Utils.chopTrailingSlash(configuration.getUrl())
                    + "/httpAuth/app/rest/buildQueue", HttpMethod.POST,
                    new HttpEntity<Build>(build, headers), String.class);

            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                log.error("Http status: {} message: {}", exchange.getStatusCode().value(),
                    exchange.getBody());
            }
        }
        catch (RestClientException e)
        {
            log.error("Error message: {}", e.getMessage());
        }
    }

    private HttpHeaders getHttpHeaders(TeamCityTriggerConfiguration configuration)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException
    {
        HttpHeaders headers = Utils.createHeaders(configuration.getUsername(),
            SecurityUtils.decrypt(configuration.getSalt(), configuration.getSecret()));
        headers.add("Accept", "application/json");
        headers.setOrigin(configuration.getUrl());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private boolean validForTrigger(RefChange refChange)
    {
        return RefChangeType.UPDATE.equals(refChange.getType())
            && "BRANCH".equals(refChange.getRef().getType().toString());
    }

    private boolean validForTrigger(PullRequestEvent event)
    {
        return event.getPullRequest().getId() > 0
            && (PullRequestAction.OPENED.equals(event.getAction())
                || PullRequestAction.REOPENED.equals(event.getAction())
                || PullRequestAction.UPDATED.equals(event.getAction()));
    }
}
