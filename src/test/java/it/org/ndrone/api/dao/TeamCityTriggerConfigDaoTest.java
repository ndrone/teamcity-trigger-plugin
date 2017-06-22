package it.org.ndrone.api.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ndrone.api.dao.TeamCityTriggerConfigDao;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;

import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TeamCityTriggerConfigDaoTest
{
    private EntityManager            entityManager;
    private ActiveObjects            activeObjects;
    private TeamCityTriggerConfigDao dao;

    @Before
    public void setUp()
    {
        Assert.assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        dao = new TeamCityTriggerConfigDao(activeObjects);
        activeObjects.migrate(TeamCityTriggerConfiguration.class);
    }

    @Test
    public void testFindNotFound()
    {
        Assert.assertEquals(0, dao.find(1).length);
    }

    @Test
    public void saveFindGetUpdateDelete()
    {
        TeamCityTriggerConfiguration save = dao.save(1, "test", "test", "test", "test", "1",
            "test");
        Assert.assertNotNull(save.getID());

        Assert.assertEquals(1, dao.find(1).length);
        Assert.assertNotNull(dao.get(save.getID()));

        String secret = save.getSecret();
        save.setSecret("Secret");
        dao.update(save);
        Assert.assertFalse("Secret should be different",
            secret.equals(dao.get(save.getID()).getSecret()));

        dao.delete(save);
        save = dao.get(save.getID());
        Assert.assertNull(save);
    }
}
