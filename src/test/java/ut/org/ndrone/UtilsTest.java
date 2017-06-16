package ut.org.ndrone;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.Utils;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilsTest
{

    @Test
    public void createHeaders()
    {
        HttpHeaders headers = Utils.createHeaders("admin", "admin");
        Assert.assertNotNull(headers);
        Assert.assertEquals(1, headers.size());

        List<String> authorization = headers.get("Authorization");
        Assert.assertNotNull(authorization);
        Assert.assertEquals(1, authorization.size());

        String value = authorization.get(0);
        Assert.assertNotNull(value);
        Assert.assertTrue(value.startsWith("Basic "));
        Assert.assertTrue(value.length() > 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chopTrailingSlashException()
    {
        Utils.chopTrailingSlash(null);
    }

    @Test
    public void chopTrailingSlash()
    {
        Assert.assertEquals("test", Utils.chopTrailingSlash("test"));
        Assert.assertEquals("test", Utils.chopTrailingSlash("test/"));
    }

    @Test
    public void validateNullUser()
    {
        UserManager userManager = Mockito.mock(UserManager.class);
        Mockito.when(userManager.getRemoteUser()).thenReturn(null);

        Assert.assertFalse("null users return false", Utils.validateUser(userManager));
    }

    @Test
    public void validateNonAdminUsers()
    {
        UserManager userManager = Mockito.mock(UserManager.class);
        UserProfile userProfile = Mockito.mock(UserProfile.class);

        Mockito.when(userProfile.getUserKey()).thenReturn(new UserKey("test"));
        Mockito.when(userManager.getRemoteUser()).thenReturn(userProfile);
        Mockito.when(userManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        Mockito.when(userManager.isAdmin(Mockito.any(UserKey.class))).thenReturn(false);

        Assert.assertFalse("Non admins return false", Utils.validateUser(userManager));
    }

    @Test
    public void validateSystemAdmin()
    {
        UserManager userManager = Mockito.mock(UserManager.class);
        UserProfile userProfile = Mockito.mock(UserProfile.class);

        Mockito.when(userProfile.getUserKey()).thenReturn(new UserKey("test"));
        Mockito.when(userManager.getRemoteUser()).thenReturn(userProfile);
        Mockito.when(userManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        Assert.assertTrue("System admins return true", Utils.validateUser(userManager));
    }

    @Test
    public void validateProjectAdmin()
    {
        UserManager userManager = Mockito.mock(UserManager.class);
        UserProfile userProfile = Mockito.mock(UserProfile.class);

        Mockito.when(userProfile.getUserKey()).thenReturn(new UserKey("test"));
        Mockito.when(userManager.getRemoteUser()).thenReturn(userProfile);
        Mockito.when(userManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        Mockito.when(userManager.isAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        Assert.assertTrue("Non admins return true", Utils.validateUser(userManager));
    }
}
