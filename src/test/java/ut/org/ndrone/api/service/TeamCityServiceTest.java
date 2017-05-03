package ut.org.ndrone.api.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.api.TeamCity;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.ndrone.api.service.TeamCityService;
import org.ndrone.api.service.TeamCityServiceImpl;

import com.atlassian.bitbucket.repository.Repository;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamCityServiceTest
{
    private TeamCityService          service;
    private TeamCityTriggerConfigDao dao;

    @Before
    public void setUp()
    {
        dao = Mockito.mock(TeamCityTriggerConfigDao.class);
        Mockito.reset(dao);
        service = new TeamCityServiceImpl(dao);
    }

    @Test
    public void findNoPreviousObject()
    {
        Repository repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getId()).thenReturn(1);
        setupNoConfigurations();

        TeamCity teamCity = service.find(repository);
        Assert.assertEquals("1", teamCity.getId());
        Assert.assertNull(teamCity.getBuildConfigId());
        Assert.assertNull(teamCity.getUsername());
        Assert.assertNull(teamCity.getPassword());
        Assert.assertNull(teamCity.getUrl());
    }

    @Test
    public void find()
    {
        Repository repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getId()).thenReturn(1);
        setupConfigurations();

        TeamCity teamCity = service.find(repository);
        Assert.assertEquals("1", teamCity.getId());
        Assert.assertEquals("1", teamCity.getBuildConfigId());
        Assert.assertEquals("test", teamCity.getUsername());
        Assert.assertEquals("test", teamCity.getPassword());
        Assert.assertEquals("test", teamCity.getUrl());
    }

    @Test
    public void deleteObjectNonExistent()
    {
        setupNoConfigurations();

        service.delete(new TeamCity.Builder().withId("1").build());
        Mockito.verify(dao, Mockito.never())
            .delete(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void delete()
    {
        setupConfigurations();

        service.delete(new TeamCity.Builder().withId("1").build());
        Mockito.verify(dao, Mockito.times(1))
            .delete(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void saveNewObject()
    {
        setupNoConfigurations();

        service.save(new TeamCity.Builder().withId("1").withBuildConfigId("1").withUsername("test")
            .withPassword("test").withUrl("test").build());
        Mockito.verify(dao, Mockito.times(1)).save(Mockito.eq(1), Mockito.eq("1"),
            Mockito.eq("test"), Mockito.eq("test"), Mockito.eq("test"));
        Mockito.verify(dao, Mockito.never())
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void updateSameObject()
    {
        setupConfigurations();

        service.save(new TeamCity.Builder().withId("1").withBuildConfigId("1").withUsername("test")
            .withPassword("test").withUrl("test").build());
        Mockito.verify(dao, Mockito.never()).save(Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(dao, Mockito.never())
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void update()
    {
        setupConfigurations();

        service.save(new TeamCity.Builder().withId("1").withBuildConfigId("2").withUsername("test")
            .withPassword("test").withUrl("test").build());
        Mockito.verify(dao, Mockito.never()).save(Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(dao, Mockito.times(1))
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    private void setupNoConfigurations()
    {
        Mockito.when(dao.find(Mockito.anyInt())).thenReturn(new TeamCityTriggerConfiguration[]{});
    }

    private void setupConfigurations()
    {
        TeamCityTriggerConfiguration teamCityTriggerConfiguration = mocked();
        Mockito.when(dao.find(Mockito.eq(1))).thenReturn(new TeamCityTriggerConfiguration[]{
            teamCityTriggerConfiguration});
    }

    private TeamCityTriggerConfiguration mocked()
    {
        TeamCityTriggerConfiguration configuration = Mockito
            .mock(TeamCityTriggerConfiguration.class);
        Mockito.when(configuration.getReposId()).thenReturn(1);
        Mockito.when(configuration.getBuildConfigId()).thenReturn("1");
        Mockito.when(configuration.getUsername()).thenReturn("test");
        Mockito.when(configuration.getSecret()).thenReturn("test");
        Mockito.when(configuration.getUrl()).thenReturn("test");
        return configuration;
    }
}
