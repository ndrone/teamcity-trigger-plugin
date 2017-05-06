package ut.org.ndrone.api.service;

import com.atlassian.bitbucket.repository.Repository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.SecurityUtils;
import org.ndrone.api.TeamCity;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;
import org.ndrone.api.service.TeamCityService;
import org.ndrone.api.service.TeamCityServiceImpl;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        Assert.assertNull(teamCity.getBuildConfigName());
    }

    @Test
    public void find()
    {
        Repository repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getId()).thenReturn(1);
        try
        {
            setupConfigurations();
        }
        catch (Exception e)
        {
            Assert.fail();
        }

        TeamCity teamCity = service.find(repository);
        Assert.assertEquals("1", teamCity.getId());
        Assert.assertEquals("1", teamCity.getBuildConfigId());
        Assert.assertEquals("test", teamCity.getUsername());
        Assert.assertNotNull(teamCity.getPassword());
        Assert.assertEquals("test", teamCity.getUrl());
        Assert.assertEquals("test", teamCity.getBuildConfigName());
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
        try
        {
            setupConfigurations();
        }
        catch (Exception e)
        {
            Assert.fail();
        }

        service.delete(new TeamCity.Builder().withId("1").build());
        Mockito.verify(dao, Mockito.times(1))
            .delete(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void saveNewObject()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        setupNoConfigurations();

        service.save(new TeamCity.Builder().withId("1").withUsername("test").withPassword("test")
            .withUrl("test").withBuildConfigId("1").withBuildConfigName("test").build());

        Mockito.verify(dao, Mockito.times(1)).save(Mockito.eq(1), Mockito.eq("test"),
            Mockito.anyString(), Mockito.anyString(), Mockito.eq("test"), Mockito.eq("1"),
            Mockito.eq("test"));
        Mockito.verify(dao, Mockito.never())
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void updateSameObject()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        Repository repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getId()).thenReturn(1);
        setupConfigurations();

        service.save(service.find(repository));

        Mockito.verify(dao, Mockito.never()).save(Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
        Mockito.verify(dao, Mockito.never())
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void update()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        setupConfigurations();

        service.save(new TeamCity.Builder().withId("1").withUsername("test").withPassword("test")
            .withUrl("test").withBuildConfigId("2").withBuildConfigName("test").build());

        Mockito.verify(dao, Mockito.never()).save(Mockito.anyInt(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
        Mockito.verify(dao, Mockito.times(1))
            .update(Mockito.any(TeamCityTriggerConfiguration.class));
    }

    @Test
    public void comparePasswordNoDatabase()
        throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException
    {
        setupNoConfigurations();

        Assert.assertEquals("test", service
            .comparePassword(new TeamCity.Builder().withId("1").withPassword("test").build()));
    }

    @Test
    public void comparePasswordChanged()
    {
        try
        {
            setupConfigurations();

            TeamCity teamCity = new TeamCity.Builder().withId("1").withUsername("test")
                .withPassword("changed").withUrl("test").withBuildConfigId("test")
                .withBuildConfigName("test").build();
            Assert.assertEquals("changed", service.comparePassword(teamCity));
        }
        catch (Exception e)
        {
            Assert.fail();
        }
    }

    private void setupNoConfigurations()
    {
        Mockito.when(dao.find(Mockito.anyInt())).thenReturn(new TeamCityTriggerConfiguration[]{});
    }

    private void setupConfigurations()
        throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException
    {
        TeamCityTriggerConfiguration teamCityTriggerConfiguration = mocked();
        Mockito.when(dao.find(Mockito.eq(1))).thenReturn(new TeamCityTriggerConfiguration[]{
            teamCityTriggerConfiguration});
    }

    private TeamCityTriggerConfiguration mocked()
        throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
        BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException
    {
        TeamCityTriggerConfiguration configuration = Mockito
            .mock(TeamCityTriggerConfiguration.class);

        Mockito.when(configuration.getReposId()).thenReturn(1);
        Mockito.when(configuration.getUsername()).thenReturn("test");

        Mockito.when(configuration.getSecret())
            .thenReturn(SecurityUtils.encrypt(SecurityUtils.generateSalt(), "test"));

        Mockito.when(configuration.getUrl()).thenReturn("test");

        Mockito.when(configuration.getBuildConfigId()).thenReturn("1");
        Mockito.when(configuration.getBuildConfigName()).thenReturn("test");
        return configuration;
    }
}
