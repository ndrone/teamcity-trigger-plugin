package org.ndrone.api;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserProfile;

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
        this.userService = userService;
        this.permissionService = permissionService;
    }

    public boolean isUserRepositoryAdmin(UserProfile userProfile, Repository repository)
    {
        return !(userProfile == null || repository == null) && permissionService
                .hasRepositoryPermission(userService.getUserByName(userProfile.getUsername()),
                        repository, Permission.ADMIN);
    }


}
