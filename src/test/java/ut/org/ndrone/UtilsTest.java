package ut.org.ndrone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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

        List<String> authorization = headers.get(Utils.AUTHORIZATION);
        Assert.assertNotNull(authorization);
        Assert.assertEquals(1, authorization.size());

        String value = authorization.get(0);
        Assert.assertNotNull(value);
        Assert.assertTrue(value.startsWith(Utils.BASIC));
        Assert.assertTrue(value.length() > Utils.BASIC.length());
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
}
