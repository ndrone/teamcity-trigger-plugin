package org.ndrone.api.model;

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
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private String url;

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
}
