package ut.org.ndrone.event;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.event.repository.RepositoryRefsChangedEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestAction;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.SecurityUtils;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.ndrone.api.service.TeamCityService;
import org.ndrone.event.GitEventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author Nicholas Drone on 6/21/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GitEventListenerTest
{
    private static final String   URL             = "http://localhost:8111";

    private MockRestServiceServer server;
    private RestTemplate          restTemplate;

    private TeamCityService       teamCityService = Mockito.mock(TeamCityService.class);

    private GitEventListener      gitEventListener;

    @Before
    public void setUp()
    {
        restTemplate = Mockito.spy(new RestTemplate());

        server = MockRestServiceServer.bindTo(restTemplate).build();
        Mockito.reset(restTemplate, teamCityService);

        gitEventListener = new GitEventListener(teamCityService, restTemplate);
    }

    @Test
    public void changeEventNotSetupForTeamCity()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException
    {
        gitEventListener.changeEvent(Mockito.mock(RepositoryRefsChangedEvent.class));
        validateQueueBuild(0);
    }

    @Test
    public void changeEventPartialSetupForTeamCity()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException
    {
        RepositoryRefsChangedEvent repositoryRefsChangedEvent = Mockito
            .mock(RepositoryRefsChangedEvent.class);

        Mockito.when(repositoryRefsChangedEvent.getRepository())
            .thenReturn(Mockito.mock(Repository.class));
        Mockito.when(teamCityService.getConfiguration(Mockito.any(Repository.class)))
            .thenReturn(Mockito.mock(TeamCityTriggerConfiguration.class));

        gitEventListener.changeEvent(repositoryRefsChangedEvent);
        validateQueueBuild(0);
    }

    @Test
    public void changeEvent()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        Repository repository = Mockito.mock(Repository.class);
        RepositoryRefsChangedEvent repositoryRefsChangedEvent = buildRefsChangedEvent(repository);

        TeamCityTriggerConfiguration configuration = buildConfiguration();
        Mockito.when(teamCityService.getConfiguration(Mockito.eq(repository)))
            .thenReturn(configuration);

        server.expect(ExpectedCount.times(1), MockRestRequestMatchers.requestTo(URL
            + GitEventListener.BUILD_QUEUE_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withStatus(HttpStatus.valueOf(200)));

        gitEventListener.changeEvent(repositoryRefsChangedEvent);
        validateQueueBuild(1);
    }

    private RepositoryRefsChangedEvent buildRefsChangedEvent(Repository repository)
    {
        RepositoryRefsChangedEvent repositoryRefsChangedEvent = Mockito
                .mock(RepositoryRefsChangedEvent.class);

        Mockito.when(repositoryRefsChangedEvent.getRepository()).thenReturn(repository);
        Mockito.when(repositoryRefsChangedEvent.getRefChanges()).thenAnswer(invocation -> {
            RefChange refChange = Mockito.mock(RefChange.class);
            Mockito.when(refChange.getType()).thenReturn(RefChangeType.UPDATE);

            MinimalRef minimalRef = Mockito.mock(MinimalRef.class);
            Mockito.when(refChange.getRef()).thenReturn(minimalRef);

            Mockito.when(minimalRef.getDisplayId()).thenReturn("test");

            RefType refType = Mockito.mock(RefType.class);
            Mockito.when(minimalRef.getType()).thenReturn(refType);
            Mockito.when(refType.toString()).thenReturn("BRANCH");


            return Arrays.asList(Mockito.mock(RefChange.class), refChange);
        });

        return repositoryRefsChangedEvent;
    }

    @Test
    public void pullRequest() throws BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException,
            InvalidKeyException
    {
        Repository repository = Mockito.mock(Repository.class);
        PullRequestEvent pullRequestEvent = buildPullRequestEvent(repository);

        TeamCityTriggerConfiguration configuration = buildConfiguration();
        Mockito.when(teamCityService.getConfiguration(Mockito.eq(repository)))
                .thenReturn(configuration);

        server.expect(ExpectedCount.times(1), MockRestRequestMatchers.requestTo(URL
                + GitEventListener.BUILD_QUEUE_URL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.valueOf(200)));

        gitEventListener.pullRequestEvent(pullRequestEvent);
        validateQueueBuild(1);

    }

    private PullRequestEvent buildPullRequestEvent(Repository repository)
    {
        PullRequestEvent pullRequestEvent = Mockito.mock(PullRequestEvent.class);

        PullRequest pullRequest = Mockito.mock(PullRequest.class);
        Mockito.when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
        Mockito.when(pullRequestEvent.getAction()).thenReturn(PullRequestAction.OPENED);

        PullRequestRef pullRequestRef = Mockito.mock(PullRequestRef.class);
        Mockito.when(pullRequest.getToRef()).thenReturn(pullRequestRef);
        Mockito.when(pullRequest.getId()).thenReturn(1L);
        Mockito.when(pullRequestRef.getRepository()).thenReturn(repository);

        return pullRequestEvent;
    }

    private TeamCityTriggerConfiguration buildConfiguration()
        throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
        BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException
    {
        String salt = SecurityUtils.generateSalt();
        String password = SecurityUtils.encrypt(salt, "test");
        TeamCityTriggerConfiguration teamCityTriggerConfiguration = Mockito
            .mock(TeamCityTriggerConfiguration.class);

        Mockito.when(teamCityTriggerConfiguration.getUsername()).thenReturn("test");
        Mockito.when(teamCityTriggerConfiguration.getSalt()).thenReturn(salt);
        Mockito.when(teamCityTriggerConfiguration.getSecret()).thenReturn(password);
        Mockito.when(teamCityTriggerConfiguration.getUrl()).thenReturn(URL);
        Mockito.when(teamCityTriggerConfiguration.getBuildConfigId()).thenReturn("1");

        return teamCityTriggerConfiguration;
    }

    private void validateQueueBuild(int wantedNumberOfInvocations)
    {
        Mockito.verify(restTemplate, Mockito.times(wantedNumberOfInvocations))
            .exchange(Mockito.eq(URL
                + GitEventListener.BUILD_QUEUE_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
                Mockito.eq(String.class));
    }
}
