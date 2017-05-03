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
