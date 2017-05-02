package org.ndrone;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

/**
 * @author Nicholas Drone on 5/1/17.
 */
public final class Utils
{
    public static HttpHeaders createHeaders(final String username, final String password)
    {
        return new HttpHeaders()
        {
            {
                String auth = username
                        + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                String authHeader = "Basic "
                        + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
    }

    public static boolean validateUser(UserManager userManager)
    {
        UserProfile user = userManager.getRemoteUser();
        return !(user == null
                || !userManager.isSystemAdmin(user.getUserKey()));
    }
}
