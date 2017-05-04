package ut.org.ndrone.api;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.api.RestResource;
import org.ndrone.api.TeamCity;
import org.ndrone.api.service.TeamCityService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;

/**
 * @author Nicholas Drone on 5/1/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RestResourceTest
{
    private static final String   URL             = "http://localhost:8111";

    private RestResource          restResource;
    private UserManager           userManager     = Mockito.mock(UserManager.class);
    private TeamCityService       teamCityService = Mockito.mock(TeamCityService.class);
    private MockRestServiceServer server;
    private RestTemplate          restTemplate;

    @Before
    public void setUp()
    {
        restTemplate = Mockito.spy(new RestTemplate());
        server = MockRestServiceServer.bindTo(restTemplate).build();

        Mockito.reset(userManager, restTemplate, teamCityService);
        restResource = new RestResource(userManager, teamCityService, restTemplate);
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
    {
        setupBitbucketUser(false);
        Response response = restResource.save(new TeamCity());
        Assert.assertEquals(401, response.getStatus());
        Mockito.verify(teamCityService, Mockito.never()).save(Mockito.any(TeamCity.class));
    }

    @Test
    public void save()
    {
        setupBitbucketUser(true);
        Response response = restResource.save(new TeamCity());
        Assert.assertEquals(200, response.getStatus());
        Mockito.verify(teamCityService, Mockito.times(1)).save(Mockito.any(TeamCity.class));
    }

    private void setupBitbucketUser(boolean admin)
    {
        UserProfile userProfile = Mockito.mock(UserProfile.class);
        UserKey userKey = new UserKey("testuser");
        Mockito.when(userProfile.getUserKey()).thenReturn(userKey);

        Mockito.when(userManager.getRemoteUser()).thenReturn(userProfile);

        Mockito.when(userManager.isSystemAdmin(userKey)).thenReturn(admin);
    }

    private TeamCity getTeamCity(String url)
    {
        TeamCity teamCity = new TeamCity();
        teamCity.setUsername("test");
        teamCity.setPassword("password");
        teamCity.setUrl(url);
        return teamCity;
    }

    private void testConnection(TeamCity teamCity, int expectedStatusCode,
        int wantedNumberOfInvocations)
    {
        String url = URL
            + "/httpAuth/app/rest/latest";

        if (wantedNumberOfInvocations > 0)
        {
            server
                .expect(ExpectedCount.times(wantedNumberOfInvocations),
                    MockRestRequestMatchers.requestTo(url))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET)).andRespond(
                    MockRestResponseCreators.withStatus(HttpStatus.valueOf(expectedStatusCode)));
        }

        Response response = restResource.testConnection(teamCity);

        Assert.assertEquals(expectedStatusCode, response.getStatus());

        Mockito.verify(restTemplate, Mockito.times(wantedNumberOfInvocations)).exchange(
            Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.<HttpEntity<?>> any(),
            Mockito.eq(String.class));
    }

}
