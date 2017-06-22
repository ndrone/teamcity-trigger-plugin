package ut.org.ndrone.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.api.BuildTypes;
import org.ndrone.api.RestResource;
import org.ndrone.api.TeamCity;
import org.ndrone.api.UserValidationService;
import org.ndrone.api.service.TeamCityService;
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
import javax.ws.rs.core.Response;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nicholas Drone on 5/1/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RestResourceTest
{
    private static final String   URL                   = "http://localhost:8111";

    private RestResource          restResource;
    private UserManager           userManager           = Mockito.mock(UserManager.class);
    private RepositoryService     repositoryService     = Mockito.mock(RepositoryService.class);
    private TeamCityService       teamCityService       = Mockito.mock(TeamCityService.class);
    private UserValidationService userValidationService = Mockito.mock(UserValidationService.class);
    private MockRestServiceServer server;
    private RestTemplate          restTemplate;

    @Before
    public void setUp()
    {
        restTemplate = Mockito.spy(new RestTemplate());
        server = MockRestServiceServer.bindTo(restTemplate).build();

        Mockito.reset(userManager, repositoryService, restTemplate, teamCityService,
            userValidationService);
        restResource = new RestResource(userManager, repositoryService, teamCityService,
            userValidationService, restTemplate);
    }

    @Test
    public void bitbucketNonAdmin()
    {
        setupBitbucketUser(false);
        testConnection(getTeamCity(URL), 401, 0);
    }

    @Test
    public void unAuthorized()
    {
        setupBitbucketUser(true);
        testConnection(getTeamCity(URL), 401, 1);
    }

    @Test
    public void unAuthorizedTrailingSlash()
    {
        setupBitbucketUser(true);
        testConnection(getTeamCity(URL
            + "/"), 401, 1);
    }

    @Test
    public void success()
    {
        setupBitbucketUser(true);
        testConnection(getTeamCity(URL), 200, 1);
    }

    @Test
    public void saveNonAdmin()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        setupBitbucketUser(false);
        TeamCity teamCity = new TeamCity.Builder().withId(RandomStringUtils.randomNumeric(1))
            .build();

        Response response = restResource.save(teamCity);
        Assert.assertEquals(401, response.getStatus());
        Mockito.verify(teamCityService, Mockito.never()).save(Mockito.any(TeamCity.class));
    }

    @Test
    public void save()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        setupBitbucketUser(true);
        TeamCity teamCity = new TeamCity.Builder().withId(RandomStringUtils.randomNumeric(1))
            .build();

        Response response = restResource.save(teamCity);
        Assert.assertEquals(200, response.getStatus());
        Mockito.verify(teamCityService, Mockito.times(1)).save(Mockito.any(TeamCity.class));
    }

    @Test
    public void fetchBuildsNonAdmin()
    {
        setupBitbucketUser(false);
        fetchBuilds(getTeamCity(URL), 401, 0);
    }

    @Test
    public void fetchBuildsAdmin()
    {
        setupBitbucketUser(true);
        fetchBuilds(getTeamCity(URL), 200, 1);
    }

    @Test
    public void deleteNonAdmin()
    {
        setupBitbucketUser(false);
        TeamCity teamCity = new TeamCity.Builder().withId(RandomStringUtils.randomNumeric(1))
            .build();

        Response response = restResource.delete(teamCity);
        Assert.assertEquals(401, response.getStatus());
        Mockito.verify(teamCityService, Mockito.never()).delete(Mockito.any(TeamCity.class));
    }

    @Test
    public void deleteAdmin()
    {
        setupBitbucketUser(true);
        TeamCity teamCity = new TeamCity.Builder().withId(RandomStringUtils.randomNumeric(1))
            .build();

        Response response = restResource.delete(teamCity);
        Assert.assertEquals(200, response.getStatus());
        Mockito.verify(teamCityService, Mockito.times(1)).delete(Mockito.any(TeamCity.class));
    }

    private void setupBitbucketUser(boolean admin)
    {
        Mockito.when(userManager.getRemoteUser()).thenReturn(Mockito.mock(UserProfile.class));
        Mockito.when(repositoryService.getById(Mockito.anyInt()))
            .thenReturn(Mockito.mock(Repository.class));

        Mockito.when(userValidationService.isUserRepositoryAdmin(Mockito.any(UserProfile.class),
            Mockito.any(Repository.class))).thenReturn(admin);
    }

    private TeamCity getTeamCity(String url)
    {
        TeamCity teamCity = new TeamCity();
        teamCity.setId("1");
        teamCity.setUsername("test");
        teamCity.setPassword("password");
        teamCity.setUrl(url);
        return teamCity;
    }

    private void testConnection(TeamCity teamCity, int expectedStatusCode,
        int wantedNumberOfInvocations)
    {
        String authCheckUrl = URL
            + RestResource.AUTH_URL_CHECK;

        serverExpectations(expectedStatusCode, wantedNumberOfInvocations, authCheckUrl);

        Response response = restResource.testConnection(teamCity);

        Assert.assertEquals(expectedStatusCode, response.getStatus());

        Mockito.verify(restTemplate, Mockito.times(wantedNumberOfInvocations)).exchange(
            Mockito.eq(authCheckUrl), Mockito.eq(HttpMethod.GET), Mockito.any(),
            Mockito.eq(String.class));
    }

    private void fetchBuilds(TeamCity teamCity, int expectedStatusCode,
        int wantedNumberOfInvocations)
    {
        String fetchBuildUrl = URL
            + RestResource.FETCH_BUILDTYPES_URL;

        serverExpectations(expectedStatusCode, wantedNumberOfInvocations, fetchBuildUrl);

        Response response = restResource.fetchBuilds(teamCity);

        Assert.assertEquals(expectedStatusCode, response.getStatus());

        Mockito.verify(restTemplate, Mockito.times(wantedNumberOfInvocations)).exchange(
            Mockito.eq(fetchBuildUrl), Mockito.eq(HttpMethod.GET), Mockito.any(),
            Mockito.eq(BuildTypes.class));

    }

    private void serverExpectations(int expectedStatusCode, int wantedNumberOfInvocations,
        String url)
    {
        if (wantedNumberOfInvocations > 0)
        {
            server
                .expect(ExpectedCount.times(wantedNumberOfInvocations),
                    MockRestRequestMatchers.requestTo(url))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET)).andRespond(
                    MockRestResponseCreators.withStatus(HttpStatus.valueOf(expectedStatusCode)));
        }
    }

}
