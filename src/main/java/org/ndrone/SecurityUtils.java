package org.ndrone;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nicholas Drone on 5/5/17.
 */
public final class SecurityUtils
{
    private static final String AES      = "AES";
    private static final int    KEY_SIZE = 128;

    public static String generateSalt() throws NoSuchAlgorithmException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(KEY_SIZE);
        return byteArrayToHexString(keyGenerator.generateKey().getEncoded());
    }

    public static String encrypt(String key, String password)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hexStringToByteArray(key), AES),
            cipher.getParameters());
        return byteArrayToHexString(cipher.doFinal(password.getBytes()));
    }

    public static String decrypt(String key, String password)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hexStringToByteArray(key), AES));
        return new String(cipher.doFinal(hexStringToByteArray(password)));
    }

    private static String byteArrayToHexString(byte[] b)
    {
        StringBuffer sb = new StringBuffer(b.length
            * 2);
        for (byte aB : b)
        {
            int v = aB
                & 0xff;
            if (v < 16)
            {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s)
    {
        byte[] b = new byte[s.length()
            / 2];
        for (int i = 0; i < b.length; i++)
        {
            int index = i
                * 2;
            int v = Integer.parseInt(s.substring(index, index
                + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}
