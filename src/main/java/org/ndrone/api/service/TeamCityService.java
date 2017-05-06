package org.ndrone.api.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.bitbucket.repository.Repository;
import org.ndrone.api.TeamCity;
import org.ndrone.api.dao.TeamCityTriggerConfiguration;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@Transactional
public interface TeamCityService
{
    TeamCity find(Repository repository);

    TeamCityTriggerConfiguration getConfiguration(Repository repository);

    void save(TeamCity teamCity)
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException;

    void delete(TeamCity teamCity);

    /**
     * Compares the password that's stored in the database if it is
     * @param teamCity object that contains the password to compare to
     * @return the password that should be used connecting to teamcity decrypted
     */
    String comparePassword(TeamCity teamCity)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException;
}
