package ut.org.ndrone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ndrone.SecurityUtils;

/**
 * @author Nicholas Drone on 5/5/17.
 */
@RunWith(JUnit4.class)
public class SecurityUtilsTest
{

    @Test
    public void Encryption()
    {
        try
        {
            String key = SecurityUtils.generateSalt();
            Assert.assertNotNull(key);

            String encrypt = SecurityUtils.encrypt(key, "password");
            Assert.assertNotNull(encrypt);
            Assert.assertTrue(!encrypt.equals("password"));

            Assert.assertEquals("password", SecurityUtils.decrypt(key, encrypt));

        }
        catch (Exception e)
        {
            Assert.fail();
        }
    }
}
