package org.ndrone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.core.util.Assert;

/**
 * @author Nicholas Drone on 5/1/17.
 */
public final class Utils
{
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static HttpHeaders createHeaders(final String username, final String password)
    {
        HttpHeaders headers = new HttpHeaders();
        String auth = username
            + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        headers.set("Authorization", "Basic "
            + new String(encodedAuth));
        return headers;
    }

    public static boolean validateUser(UserManager userManager)
    {
        UserProfile user = userManager.getRemoteUser();

        if (user == null)
        {
            log.error("User is null.");
            return false;
        }
        else if (userManager.isSystemAdmin(user.getUserKey()) || userManager.isAdmin(user.getUserKey()))
        {
            log.info("Valid user");
            return true;
        }
        else
        {
            log.info("User doesn't have correct permissions");
            log.debug("User: {} ", user.getUsername());
            return false;
        }
    }

    public static String chopTrailingSlash(String url)
    {
        Assert.notNull(url);
        if (url.substring(url.length()
            - 1).equals("/"))
        {
            return StringUtils.chop(url);
        }
        return url;
    }
}
