package ut.org.ndrone.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.ndrone.api.UserValidationService;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.sal.api.user.UserProfile;

/**
 * @author Nicholas Drone on 6/21/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserValidationServiceTest
{
    private static final String   USERNAME          = "test";

    private UserValidationService userValidationService;

    private UserService           userService       = Mockito.mock(UserService.class);
    private PermissionService     permissionService = Mockito.mock(PermissionService.class);

    @Before
    public void setUp()
    {
        Mockito.reset(userService, permissionService);

        userValidationService = new UserValidationService(userService, permissionService);
    }

    @Test
    public void nullArgs()
    {
        Assert.assertFalse("Should be false with null params",
            userValidationService.isUserRepositoryAdmin(null, null));

        verifyInteraction(0);
    }

    @Test
    public void userProfileNull()
    {
        Assert.assertFalse("Should be false with userProfile null",
            userValidationService.isUserRepositoryAdmin(null, Mockito.mock(Repository.class)));

        verifyInteraction(0);
    }

    @Test
    public void RepositoryNull()
    {
        Assert.assertFalse("Should be false with repository null",
            userValidationService.isUserRepositoryAdmin(Mockito.mock(UserProfile.class), null));

        verifyInteraction(0);
    }

    @Test
    public void nonAdminUser()
    {
        setupValid(false);

        Assert.assertFalse("Should be false", userValidationService
            .isUserRepositoryAdmin(getUserProfile(), Mockito.mock(Repository.class)));

        verifyInteraction(1);
    }

    @Test
    public void AdminUser()
    {
        setupValid(true);

        Assert.assertTrue("Should be true", userValidationService
            .isUserRepositoryAdmin(getUserProfile(), Mockito.mock(Repository.class)));

        verifyInteraction(1);
    }

    private void verifyInteraction(int wantedNumberOfInvocations)
    {
        Mockito.verify(permissionService, Mockito.times(wantedNumberOfInvocations))
            .hasRepositoryPermission(Mockito.any(ApplicationUser.class),
                Mockito.any(Repository.class), Mockito.eq(Permission.ADMIN));
    }

    private UserProfile getUserProfile()
    {
        UserProfile userProfile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfile.getUsername()).thenReturn(USERNAME);
        return userProfile;
    }

    private void setupValid(boolean value)
    {
        Mockito.when(userService.getUserByName(Mockito.eq(USERNAME)))
            .thenReturn(Mockito.mock(ApplicationUser.class));
        Mockito.when(permissionService.hasRepositoryPermission(Mockito.any(ApplicationUser.class),
            Mockito.any(Repository.class), Mockito.eq(Permission.ADMIN))).thenReturn(value);
    }
}
