package org.ndrone.api.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.bitbucket.repository.Repository;
import org.ndrone.api.TeamCity;

/**
 * @author Nicholas Drone on 5/2/17.
 */
@Transactional
public interface TeamCityService
{
    TeamCity find(Repository repository);

    void save(TeamCity teamCity);

    void delete(TeamCity teamCity);
}
