package org.ndrone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.atlassian.sal.core.util.Assert;

/**
 * @author Nicholas Drone on 5/1/17.
 */
public final class Utils
{

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic ";

    public static HttpHeaders createHeaders(final String username, final String password)
    {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        headers.set(AUTHORIZATION, BASIC + new String(encodedAuth));
        return headers;
    }

    public static String chopTrailingSlash(String url)
    {
        Assert.notNull(url);
        if (url.substring(url.length() - 1).equals("/"))
        {
            return StringUtils.chop(url);
        }
        return url;
    }
}
