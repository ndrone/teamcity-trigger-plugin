package org.ndrone.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Nicholas Drone on 4/29/17.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class TeamCity
{
    @XmlElement
    private String id;
    @XmlElement
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private String url;
    @XmlElement
    private String buildConfigId;

    public TeamCity()
    {
    }

    private TeamCity(Builder builder)
    {
        setId(builder.id);
        setUsername(builder.username);
        setPassword(builder.password);
        setUrl(builder.url);
        setBuildConfigId(builder.buildConfigId);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getBuildConfigId()
    {
        return buildConfigId;
    }

    public void setBuildConfigId(String buildConfigId)
    {
        this.buildConfigId = buildConfigId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null
            || getClass() != o.getClass())
            return false;

        TeamCity teamCity = (TeamCity) o;

        if (!id.equals(teamCity.id))
            return false;
        if (!username.equals(teamCity.username))
            return false;
        if (!password.equals(teamCity.password))
            return false;
        if (!url.equals(teamCity.url))
            return false;
        return buildConfigId != null
            ? buildConfigId.equals(teamCity.buildConfigId) : teamCity.buildConfigId == null;
    }

    @Override
    public int hashCode()
    {
        int result = id.hashCode();
        result = 31
            * result
            + username.hashCode();
        result = 31
            * result
            + password.hashCode();
        result = 31
            * result
            + url.hashCode();
        result = 31
            * result
            + (buildConfigId != null
                ? buildConfigId.hashCode() : 0);
        return result;
    }

    public static final class Builder
    {
        private String id;
        private String username;
        private String password;
        private String url;
        private String buildConfigId;

        public Builder()
        {
        }

        public Builder withId(String val)
        {
            id = val;
            return this;
        }

        public Builder withUsername(String val)
        {
            username = val;
            return this;
        }

        public Builder withPassword(String val)
        {
            password = val;
            return this;
        }

        public Builder withUrl(String val)
        {
            url = val;
            return this;
        }

        public Builder withBuildConfigId(String val)
        {
            buildConfigId = val;
            return this;
        }

        public TeamCity build()
        {
            return new TeamCity(this);
        }
    }
}
