package org.ndrone.api;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserProfile;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Nicholas Drone on 6/21/17.
 */
@Scanned
@Named
public class UserValidationService
{
    @ComponentImport
    private final UserService       userService;
    @ComponentImport
    private final PermissionService permissionService;

    @Inject
    public UserValidationService(UserService userService, PermissionService permissionService)
    {
        Assert.notNull(userService, "UserService must not be null");
        this.userService = userService;
        Assert.notNull(permissionService, "PermissionService must not be null");
        this.permissionService = permissionService;
    }

    public boolean isUserRepositoryAdmin(UserProfile userProfile, Repository repository)
    {
        Assert.notNull(userProfile, "UserProfile must not be null");
        Assert.notNull(repository, "Repository must not be null");
        return permissionService.hasRepositoryPermission(getApplicationUser(userProfile),
            repository, Permission.REPO_ADMIN);
    }

    private ApplicationUser getApplicationUser(UserProfile userProfile)
    {
        return userService.getUserByName(userProfile.getUsername());
    }
}
