package org.ndrone.api.dao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

/**
 * @author Nicholas Drone on 5/1/17.
 */
@Table("TTCONFIG")
@Preload
public interface TeamCityTriggerConfiguration extends Entity
{
    @NotNull
    @Accessor("REPOS_ID")
    Integer getReposId();

    @Mutator("REPOS_ID")
    void setReposId(Integer reposId);

    @Accessor("BUILD_CONFIG_ID")
    String getBuildConfigId();

    @Mutator("BUILD_CONFIG_ID")
    void setBuildConfigId(String buildConfigId);

    @NotNull
    @Accessor("USERNAME")
    String getUsername();

    @Mutator("USERNAME")
    void setUsername(String username);

    @NotNull
    @Accessor("SECRET")
    String getSecret();

    @Mutator("SECRET")
    void setSecret(String secret);

    @NotNull
    @Accessor("URL")
    String getUrl();

    @Mutator("URL")
    void setUrl(String url);

    @Accessor("BUILD_CONFIG_NAME")
    String getBuildConfigName();

    @Mutator("BUILD_CONFIG_NAME")
    void setBuildConfigName(String buildConfigName);
}
